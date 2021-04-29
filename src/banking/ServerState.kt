package banking

// Imports.
import com.google.gson.Gson

object ServerState {
    data class State(
        var accounts: List<Account> = listOf(
            Account("10001", "senha", 1000.00),
            Account("10002", "senha", 2000.00),
            Account("10003", "senha", 3000.00),
            Account("10004", "senha", 4000.00),
        ),
        val transferTariff: Double = 10.37,
        var serverId: Int = -1,
        var lastAccountId: Int = accounts.last().id.toInt()
    )

    // Set initial state
    var internalState = State()

    fun fromByteArray(byteArray: ByteArray) {
        internalState = Gson().fromJson(String(byteArray), internalState::class.java)
    }

    fun toByteArray(): ByteArray {
        return Gson().toJson(internalState).toByteArray()
    }
}