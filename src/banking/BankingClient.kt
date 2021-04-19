// Client class.

// Package.
package banking

// Imports.
import bftsmart.tom.ServiceProxy
import kotlin.jvm.JvmStatic

object BankingClient {
    @JvmStatic
    fun main(args: Array<String>) {
        val proxy = ServiceProxy(1001)

        val request = BankingClientMessage(
                BankingOperationsCode.PIX,
                "6b840607-78dd-4738-a8e8-a0ff2911125c",
                "3cdbd552-a060-4ad3-bb27-f56ad939303f",
                231.42
        ).toByteArray()

        val reply = proxy.invokeOrdered(request)
        val replyMessage = BankingServerMessage(reply)
        println("Resposta recebida: $replyMessage")
    }
}