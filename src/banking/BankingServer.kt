// Server class.

// Package.
package banking

// Imports.
import bftsmart.tom.MessageContext
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable
import bftsmart.tom.ServiceReplica
import java.lang.UnsupportedOperationException
import kotlin.jvm.JvmStatic

import operations.MapOperations

class BankingServer(id: Int) : DefaultSingleRecoverable() {
    override fun appExecuteOrdered(bytes: ByteArray, context: MessageContext): ByteArray {
        val request = BankingClientMessage(bytes)
        println("Requisição recebida: $request")

        // For test only:
        ServerState.internalState.accounts[0].value = request.operationValue

        return BankingServerMessage(BankingServerMessageResultCode.SUCCESS).toByteArray()
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

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BankingServer(args[0].toInt())
        }
    }

    init {
        ServiceReplica(id, this, this)
        ServerState.internalState.serverId = id
    }
}