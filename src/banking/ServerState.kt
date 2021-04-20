package banking

// Imports.
import com.google.gson.Gson

object ServerState {
    data class State(
        var accounts: List<Account> = listOf(
            Account("0", 1000.00),
            Account("1", 2000.00),
            Account("2", 3000.00),
            Account("3", 4000.10),
        ),
        val bankName: String = "DBB",
        var serverId: Int = -1
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