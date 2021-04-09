// Server class.

// Package.
package banking

// Imports.
import bftsmart.tom.MessageContext
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable
import bftsmart.tom.ServiceReplica
import java.lang.UnsupportedOperationException
import kotlin.jvm.JvmStatic

class BankingServer(id: Int) : DefaultSingleRecoverable() {
    override fun appExecuteOrdered(bytes: ByteArray, context: MessageContext): ByteArray {
        val request = String(bytes)
        println("Requisição recebida: $request")
        return "Resposta - $request".toByteArray()
    }

    override fun appExecuteUnordered(bytes: ByteArray, context: MessageContext): ByteArray {
        throw UnsupportedOperationException("Not supported yet.")
    }

    override fun getSnapshot(): ByteArray {
        return "".toByteArray()
    }

    override fun installSnapshot(bytes: ByteArray) {}

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BankingServer(args[0].toInt())
        }
    }

    init {
        ServiceReplica(id, this, this)
    }
}