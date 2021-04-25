// Client class.

// Package.
package banking

// Imports.
import bftsmart.tom.ServiceProxy
import kotlin.jvm.JvmStatic

object BankingClient {

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
                    println("Create your password: ")
                    val password = readLine()!!
                    val request = ClientMessage.AuthMessage(
                            AuthOperationsCode.CREATE,
                            null,
                            password
                    ).toByteArray()

                    val replyMessage = BankingServerMessage.fromByteArray(proxy.invokeOrdered(request))
                    when (replyMessage.resultCode) {
                        BankingServerMessageResultCode.SUCCESS -> {
                            println("Account created")
                        }
                        else -> {
                            print("Error while creating your account")
                        }
                    }
                }
                AuthenticationMenuActions.ACCESS -> {
                    println("Enter your account ID: ")
                    val accountId = readLine()!!
                    println("Enter your password: ")
                    val accountPassword = readLine()!!

                    val request = ClientMessage.AuthMessage(
                            AuthOperationsCode.ACCESS,
                            accountId,
                            accountPassword
                    ).toByteArray()
                    val replyMessage = BankingServerMessage.fromByteArray(proxy.invokeOrdered(request))

                    when (replyMessage.resultCode) {
                        BankingServerMessageResultCode.SUCCESS -> {
                            while (true) {
                                when(val operationOption = chooseAuthenticatedMenuOption()) {
                                    AuthenticatedMenuActions.EXIT -> {
                                        break
                                    }
                                    AuthenticatedMenuActions.WITHDRAW, AuthenticatedMenuActions.DEPOSIT-> {
                                        println("Type value: (R$)")
                                        val operationValue = readLine()!!.toDouble()
                                        val withdrawRequest = ClientMessage.BankingMessage(
                                                operationOption.toBankingOperationsCode(),
                                                accountId,
                                                null,
                                                operationValue,
                                                "JWT"
                                        ).toByteArray()
                                        val withdrawReply = BankingServerMessage.fromByteArray(proxy.invokeOrdered(withdrawRequest))
                                        if(withdrawReply.resultCode == BankingServerMessageResultCode.SUCCESS) {
                                            println("Success!")
                                        } else {
                                            println("An error happened while processing your operation, please try again later.")
                                        }
                                    }
                                    AuthenticatedMenuActions.PIX, AuthenticatedMenuActions.TRANSFER -> {
                                        println("Type value: (R$)")
                                        val operationValue = readLine()!!.toDouble()
                                        println("Type destination account: ")
                                        val targetAccount = readLine()!!
                                        val withdrawRequest = ClientMessage.BankingMessage(
                                                operationOption.toBankingOperationsCode(),
                                                accountId,
                                                targetAccount,
                                                operationValue,
                                                "JWT"
                                        ).toByteArray()
                                        val withdrawReply = BankingServerMessage.fromByteArray(proxy.invokeOrdered(withdrawRequest))
                                        if(withdrawReply.resultCode == BankingServerMessageResultCode.SUCCESS) {
                                            println("Success!")
                                        } else {
                                            println("An error happened while processing your operation, please try again later.")
                                        }
                                    }
                                }
                            }
                        }
                        BankingServerMessageResultCode.AUTHENTICATION_ERROR -> {
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

            println("Choose an option:")
            println("0) Back")
            println("1) Deposit")
            println("2) Withdraw")
            println("3) Transfer")
            println("4) PIX")
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

            println("Welcome to Distributed Banking!")
            println("Choose an option:")
            println("0) Exit")
            println("1) Access your account")
            println("2) Create an account")

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