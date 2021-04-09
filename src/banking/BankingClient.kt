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
        val request = args[0].toByteArray()
        val reply = proxy.invokeOrdered(request)
        val replyString = String(reply)
        println("Resposta recebida: $replyString")
    }
}