// Client class.

// Package.
package banking

// Imports.
import bftsmart.tom.ServiceProxy
import kotlin.jvm.JvmStatic

object BankingClient {

    private var running: Boolean = true

    @JvmStatic
    fun main(args: Array<String>) {
        val proxy = ServiceProxy(1001)
        while (running) {
            val option = chooseAuthenticatedMenuOption()

            if(option == AuthenticatedMenuActions.EXIT) {
                running = false
                continue
            }

            val request = BankingClientMessage(
                    option.toBankingOperationsCode(),
                    "6b840607-78dd-4738-a8e8-a0ff2911125c",
                    "3cdbd552-a060-4ad3-bb27-f56ad939303f",
                    231.42
            ).toByteArray()

            val replyMessage = BankingServerMessage.fromByteArray(proxy.invokeOrdered(request))
            if(replyMessage.resultCode == BankingServerMessageResultCode.SUCCESS) {
                println("Success!")
            } else {
                println("An error happened while processing your operation, please try again later.")
            }
        }
        println("Exiting...")
        proxy.close()
        println("Goodbye!")
    }

    enum class AuthenticatedMenuActions {
        EXIT, DEPOSIT, WITHDRAW, TRANSFER, PIX;

        fun toBankingOperationsCode() = BankingOperationsCode.valueOf(this.name)

        companion object {
            fun fromInt(value: Int) = values().first { it.ordinal == value }
        }
    }

    private fun chooseAuthenticatedMenuOption(): AuthenticatedMenuActions {
        var chosenOption: AuthenticatedMenuActions? = null
        while (chosenOption == null) {

            println("Choose an option:")
            println("0) Exit")
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

}