// Client class.

// Package.
package banking

// Imports.
import bftsmart.tom.ServiceProxy
import kotlin.jvm.JvmStatic

object Client {

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
                    val password = readLine()!!
                    val request = ClientMessage.AuthMessage(
                            AuthOperationsCode.CREATE,
                            null,
                            password
                    ).toByteArray()

                    val replyMessage = ServerMessage.fromByteArray(proxy.invokeOrdered(request))
                    when (replyMessage.resultCode) {
                        ServerMessageResultCode.SUCCESS -> {
                            println("Account created")
                        }
                        else -> {
                            println("Error while creating your account")
                        }
                    }
                }
                AuthenticationMenuActions.ACCESS -> {
                    print("Enter your account ID: ")
                    val accountId = readLine()!!
                    print("Enter your password: ")
                    val accountPassword = readLine()!!

                    val request = ClientMessage.AuthMessage(
                            AuthOperationsCode.ACCESS,
                            accountId,
                            accountPassword
                    ).toByteArray()
                    val replyMessage = ServerMessage.fromByteArray(proxy.invokeOrdered(request))

                    when (replyMessage.resultCode) {
                        ServerMessageResultCode.SUCCESS -> {
                            while (true) {
                                when(val operationOption = chooseAuthenticatedMenuOption()) {
                                    AuthenticatedMenuActions.EXIT -> {
                                        break
                                    }
                                    AuthenticatedMenuActions.WITHDRAW, AuthenticatedMenuActions.DEPOSIT-> {
                                        print("Type value (R$): ")
                                        val operationValue = readLine()!!.toDouble()
                                        val withdrawRequest = ClientMessage.BankingMessage(
                                                operationOption.toBankingOperationsCode(),
                                                accountId,
                                                null,
                                                operationValue,
                                                "JWT"
                                        ).toByteArray()
                                        val withdrawReply = ServerMessage.fromByteArray(proxy.invokeOrdered(withdrawRequest))
                                        if(withdrawReply.resultCode == ServerMessageResultCode.SUCCESS) {
                                            println("Success!")
                                        } else {
                                            println("An error happened while processing your operation, please try again later.")
                                        }
                                    }
                                    AuthenticatedMenuActions.PIX, AuthenticatedMenuActions.TRANSFER -> {
                                        print("Type value (R$): ")
                                        val operationValue = readLine()!!.toDouble()
                                        print("Type destination account: ")
                                        val targetAccount = readLine()!!
                                        val withdrawRequest = ClientMessage.BankingMessage(
                                                operationOption.toBankingOperationsCode(),
                                                accountId,
                                                targetAccount,
                                                operationValue,
                                                "JWT"
                                        ).toByteArray()
                                        val withdrawReply = ServerMessage.fromByteArray(proxy.invokeOrdered(withdrawRequest))
                                        if(withdrawReply.resultCode == ServerMessageResultCode.SUCCESS) {
                                            println("Success!")
                                        } else {
                                            println("An error happened while processing your operation, please try again later.")
                                        }
                                    }
                                }
                            }
                        }
                        ServerMessageResultCode.AUTHENTICATION_ERROR -> {
                            println("Wrong password!")
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
            println("Account session is active.")
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
}