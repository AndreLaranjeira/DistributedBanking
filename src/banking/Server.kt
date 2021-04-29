// Server class.

// Package.
package banking

// Imports.
import bftsmart.tom.MessageContext
import bftsmart.tom.ServiceReplica
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable

class Server(id: Int) : DefaultSingleRecoverable() {
    override fun appExecuteOrdered(bytes: ByteArray, context: MessageContext): ByteArray {
        var resultMessage: ServerMessage? = null
        try {
            val clientRequest = ClientMessage.fromByteArray(bytes)
            println("Requisição recebida $clientRequest")

            when(clientRequest) {
                is ClientMessage.AuthMessage ->
                    when(clientRequest.operationCode) {
                        AuthOperationsCode.CREATE -> {
                            resultMessage = try {
                                val accountId = createAccount(clientRequest.accountPassword)
                                ServerMessage.AccountCreationMessage(ServerMessageResultCode.SUCCESS, "Account created with success!", accountId)
                            } catch (e: Exception) {
                                ServerMessage.AccountCreationMessage(ServerMessageResultCode.ERROR, "Error while creating account: " + (e.message ?: "Unknown error."), null)
                            }
                        }
                        AuthOperationsCode.ACCESS -> {
                            resultMessage = try {
                                val loginData = Auth.login(clientRequest.accountId ?: "", clientRequest.accountPassword)
                                ServerMessage.LoginMessage(ServerMessageResultCode.SUCCESS, "You are now logged in!", loginData)
                            } catch (e: Exception) {
                                ServerMessage.LoginMessage(ServerMessageResultCode.ERROR, "Error logging into your account: " + (e.message ?: "Unknown error."), null)
                            }
                        }
                    }
                is ClientMessage.BankingMessage -> {
                    try {
                        val accountInformation = Auth.validate(clientRequest.jwtToken ?: "")
                        if (accountInformation.id != clientRequest.originAccount)
                            throw RuntimeException("The origin account id must be the same as the authenticated account id")
                        var message = "Success!"
                        when (clientRequest.operationCode) {
                            BankingOperationsCode.DEPOSIT -> {
                                deposit(clientRequest.originAccount, clientRequest.operationValue)
                                message = "Successfully deposited the amount."
                            }
                            BankingOperationsCode.PIX -> {
                                pix(clientRequest.originAccount, clientRequest.targetAccount ?: "", clientRequest.operationValue)
                                message = "Successfully transfered the amount via pix."
                            }
                            BankingOperationsCode.TRANSFER -> {
                                transfer(clientRequest.originAccount, clientRequest.targetAccount ?: "", clientRequest.operationValue)
                                message = "Successfully transfered the amount."
                            }
                            BankingOperationsCode.WITHDRAW -> {
                                withdraw(clientRequest.originAccount, clientRequest.operationValue)
                                message = "Successfully withdrawed the amount."
                            }
                        }
                        val account: Account = ServerState.internalState.accounts.first{ it.id == clientRequest.originAccount }
                        resultMessage = ServerMessage.BankingMessage(ServerMessageResultCode.SUCCESS, message, account.value)
                    } catch (e: Exception) {
                        resultMessage = ServerMessage.BankingMessage(ServerMessageResultCode.ERROR, "Error realizing the operation: " + (e.message ?: "Unknown error."), null)
                    }
                }
                else -> {
                    resultMessage = ServerMessage(ServerMessageType.NONE, ServerMessageResultCode.ERROR, "Error while decoding the request, please try again later.")
                }
            }
        } catch (e: Exception) {
            resultMessage = ServerMessage(ServerMessageType.NONE, ServerMessageResultCode.ERROR, "The operation request has been corrupted.")
        }

        return (resultMessage ?: ServerMessage(ServerMessageType.NONE, ServerMessageResultCode.ERROR, "An unknown error has happened.")).toByteArray()
    }

    override fun appExecuteUnordered(bytes: ByteArray, context: MessageContext): ByteArray {
        throw UnsupportedOperationException("Not supported yet.")
    }

    override fun getSnapshot(): ByteArray {
        println(
            "[getSnapshot][internalState::accounts[0]::value]: " +
            ServerState.internalState.accounts[0].value
        )
        return ServerState.toByteArray()
    }

    override fun installSnapshot(bytes: ByteArray) {
        ServerState.fromByteArray(bytes)
        println(
            "[getSnapshot][internalState::accounts[0]::value]: " +
            ServerState.internalState.accounts[0].value
        )
        return
    }

    // Bank operations

    private fun deposit(accountId: String, amount: Double) {
        val recipientAccount = ServerState.internalState.accounts.find { account -> account.id == accountId } ?:
            throw RuntimeException("Account not found!")

        recipientAccount.value += amount
    }

    private fun pix(originAccountId: String, targetAccountId: String, amount: Double) {
        if(originAccountId == targetAccountId) throw RuntimeException("Origin and target accounts cannot be the same!")

        val originAccount = ServerState.internalState.accounts.find { account -> account.id == originAccountId } ?:
            throw RuntimeException("Origin account not found!")

        val targetAccount = ServerState.internalState.accounts.find { account -> account.id == targetAccountId } ?:
            throw RuntimeException("Target account not found!")

        if(originAccount.value < amount) throw RuntimeException("Not enough funds!")

        originAccount.value -= amount
        targetAccount.value += amount
    }

    private fun transfer(originAccountId: String, targetAccountId: String, amount: Double) {
        if(originAccountId == targetAccountId) throw RuntimeException("Origin and target accounts cannot be the same!")

        val originAccount = ServerState.internalState.accounts.find { account -> account.id == originAccountId } ?:
            throw RuntimeException("Origin account not found!")

        val targetAccount = ServerState.internalState.accounts.find { account -> account.id == targetAccountId } ?:
            throw RuntimeException("Target account not found!")

        if(originAccount.value < (amount + ServerState.internalState.transferTariff))
            throw RuntimeException("Not enough funds!")

        originAccount.value -= (amount + ServerState.internalState.transferTariff)
        targetAccount.value += amount
    }

    private fun withdraw(accountId: String, amount: Double) {
        val withdrawAccount = ServerState.internalState.accounts.find { account -> account.id == accountId } ?:
            throw RuntimeException("Account not found!")

        if(withdrawAccount.value < amount) throw RuntimeException("Not enough funds!")

        withdrawAccount.value -= amount
    }

    // Account operations

    private fun createAccount(password: String): String {
        val id = (ServerState.internalState.lastAccountId++).toString()
        ServerState.internalState.accounts += Account(id, password, 0.00)
        return id
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Server(args[0].toInt())
        }
    }

    init {
        ServiceReplica(id, this, this)
        ServerState.internalState.serverId = id
    }
}