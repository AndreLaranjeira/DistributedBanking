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

        if(originAccount.value < (amount + ServerState.internalState.bankTransferTariff))
            throw RuntimeException("Not enough funds!")

        originAccount.value -= (amount + ServerState.internalState.bankTransferTariff)
        targetAccount.value += amount
    }

    private fun withdraw(accountId: String, amount: Double) {
        val withdrawAccount = ServerState.internalState.accounts.find { account -> account.id == accountId } ?:
            throw RuntimeException("Account not found!")

        if(withdrawAccount.value < amount) throw RuntimeException("Not enough funds!")

        withdrawAccount.value -= amount
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