// Client class.

// Package.
package banking

// Imports.
import bftsmart.tom.ServiceProxy
import kotlin.jvm.JvmStatic

object Client {

    data class ClientSession (
        var jwtToken: String = "",
        var accountId: String = "",
        var accountBalance: Double = 0.0
    )
    private val clientSession = ClientSession()

    enum class AuthenticatedMenuActions {
        EXIT, DEPOSIT, WITHDRAW, TRANSFER, PIX;
        fun toBankingOperationsCode() = BankingOperationsCode.valueOf(this.name)

        companion object {
            fun fromInt(value: Int) = values().first { it.ordinal == value }
        }
    }

    enum class AuthenticationMenuActions {
        EXIT, ACCESS, CREATE;

        companion object {
            fun fromInt(value: Int) = values().first { it.ordinal == value }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val proxy = ServiceProxy(1001)

        while (true) {
            when (chooseAuthenticationMenuOption()) {
                AuthenticationMenuActions.EXIT -> {
                    break
                }
                AuthenticationMenuActions.CREATE -> {
                    print("Create your password: ")
                    val password = readLine()

                    if(password.isNullOrBlank()) {
                        println("Cannot accept an empty password! Aborting.")
                        continue
                    }

                    val request = ClientMessage.AuthMessage(
                            AuthOperationsCode.CREATE,
                            null,
                            password
                    ).toByteArray()

                    val replyMessage = ServerMessage.fromByteArray(proxy.invokeOrdered(request))
                    handleServerReplies(replyMessage)
                }
                AuthenticationMenuActions.ACCESS -> {
                    print("Enter your account ID: ")
                    val accountId = readLine()

                    if(accountId.isNullOrBlank()) {
                        println("Cannot accept an empty account ID! Aborting.")
                        continue
                    }

                    print("Enter your password: ")
                    val accountPassword = readLine()

                    if(accountPassword.isNullOrBlank()) {
                        println("Cannot accept an empty password! Aborting.")
                        continue
                    }

                    val request = ClientMessage.AuthMessage(
                            AuthOperationsCode.ACCESS,
                            accountId,
                            accountPassword
                    ).toByteArray()
                    val replyMessage = ServerMessage.fromByteArray(proxy.invokeOrdered(request))
                    val isSuccessCode = handleServerReplies(replyMessage)

                    if(isSuccessCode) {
                        while (true) {
                            when (val operationOption = chooseAuthenticatedMenuOption()) {
                                AuthenticatedMenuActions.EXIT -> {
                                    break
                                }
                                AuthenticatedMenuActions.WITHDRAW, AuthenticatedMenuActions.DEPOSIT -> {
                                    var operationValue: Double?

                                    try {
                                        print("Type value (R$): ")
                                        operationValue = readLine()!!.toDouble()
                                    } catch (e: Exception) {
                                        when (e) {
                                            is NumberFormatException, is NullPointerException -> {
                                                println("Value input must be a number! Aborting.")
                                            }
                                            else -> println("An error happened while processing your operation, please try again later.")
                                        }
                                        continue
                                    }

                                    val withdrawRequest = ClientMessage.BankingMessage(
                                            operationOption.toBankingOperationsCode(),
                                            clientSession.accountId,
                                            null,
                                            operationValue,
                                            clientSession.jwtToken
                                    ).toByteArray()
                                    val withdrawReply = ServerMessage.fromByteArray(proxy.invokeOrdered(withdrawRequest))
                                    handleServerReplies(withdrawReply)
                                }
                                AuthenticatedMenuActions.PIX, AuthenticatedMenuActions.TRANSFER -> {
                                    var operationValue: Double?

                                    try {
                                        print("Type value (R$): ")
                                        operationValue = readLine()!!.toDouble()
                                    } catch (e: Exception) {
                                        when (e) {
                                            is NumberFormatException, is NullPointerException -> {
                                                println("Value input must be a number! Aborting.")
                                            }
                                            else -> println("An error happened while processing your operation, please try again later.")
                                        }
                                        continue
                                    }

                                    print("Type destination account: ")
                                    val targetAccount = readLine()

                                    if (targetAccount.isNullOrBlank()) {
                                        println("Cannot accept an empty destination account! Aborting.")
                                        continue
                                    }

                                    val withdrawRequest = ClientMessage.BankingMessage(
                                            operationOption.toBankingOperationsCode(),
                                            clientSession.accountId,
                                            targetAccount,
                                            operationValue,
                                            clientSession.jwtToken
                                    ).toByteArray()
                                    val withdrawReply = ServerMessage.fromByteArray(proxy.invokeOrdered(withdrawRequest))
                                    handleServerReplies(withdrawReply)
                                }
                            }
                        }
                    }
                }
            }
        }
        println("Exiting...")
        proxy.close()
        println("Goodbye!")
    }

    private fun chooseAuthenticatedMenuOption(): AuthenticatedMenuActions {
        var chosenOption: AuthenticatedMenuActions? = null
        while (chosenOption == null) {

            println("")
            println("********************************")
            println("Account session is active. ID: ${clientSession.accountId}")
            println("Account balance: R$ ${clientSession.accountBalance}")
            println("Choose an option:")
            println("0) Logoff")
            println("1) Deposit")
            println("2) Withdraw")
            println("3) Transfer")
            println("4) PIX")
            println("********************************")
            print("Option: ")

            try {
                chosenOption = AuthenticatedMenuActions.fromInt(readLine()!!.toInt())
            } catch (e: Exception) {
                when(e) {
                    is NumberFormatException, is NoSuchElementException, is NullPointerException -> {
                        println("Please input a number corresponding to one of the menu's options.")
                    }
                    else -> throw e
                }
            }
        }
        return chosenOption
    }

    private fun chooseAuthenticationMenuOption() : AuthenticationMenuActions {
        var chosenOption: AuthenticationMenuActions? = null
        while (chosenOption == null) {

            println("")
            println("********************************")
            println("Welcome to Distributed Banking!")
            println("Choose an option:")
            println("0) Exit")
            println("1) Access your account")
            println("2) Create an account")
            println("********************************")
            print("Option: ")

            try {
                chosenOption = AuthenticationMenuActions.fromInt(readLine()!!.toInt())
            } catch (e: Exception) {
                when(e) {
                    is NumberFormatException, is NoSuchElementException, is NullPointerException -> {
                        println("Please input a number corresponding to one of the menu's options.")
                    }
                    else -> throw e
                }
            }
        }
        return chosenOption
    }

    private fun handleServerReplies(serverMessage: ServerMessage) : Boolean {
        try {
            if(serverMessage.resultCode == ServerMessageResultCode.ERROR) {
                println(serverMessage.errorMessage)
                return false
            }
            else {
                when(serverMessage) {
                    is ServerMessage.AccountCreationMessage -> {
                        println("Account created successfully")
                        println("Your account ID is: ${serverMessage.accountId}")
                    }
                    is ServerMessage.BankingMessage -> {
                        println("Operation successful")
                        println("Your new balance is: R$ ${serverMessage.value}")
                    }
                    is ServerMessage.LoginMessage -> {
                        println("Welcome to Distributed Banking!")
                        println("Account ID: ${serverMessage.loginData?.id}")
                        clientSession.accountId = serverMessage.loginData?.id!!
                        clientSession.accountBalance = serverMessage.loginData.value
                        clientSession.jwtToken = serverMessage.loginData.token
                    }
                }
                return true
            }
        } catch (e: Exception) {
            println("An error happened while decoding server message $e")
            return false
        }

    }

}