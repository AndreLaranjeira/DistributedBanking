// Server class.

// Package.
package banking

// Imports.
import bftsmart.tom.MessageContext
import bftsmart.tom.ServiceReplica
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable

class Server(id: Int) : DefaultSingleRecoverable() {
    override fun appExecuteOrdered(bytes: ByteArray, context: MessageContext): ByteArray {
        val clientRequest = ClientMessage.fromByteArray(bytes)
        println("Requisição recebida $clientRequest")

        // For test only:
        // ServerState.internalState.accounts[0].value = request.operationValue

        return ServerMessage(ServerMessageResultCode.SUCCESS).toByteArray()
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