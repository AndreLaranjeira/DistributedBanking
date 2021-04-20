package banking

// Imports.
import operations.MapOperations

object ServerState {
    var accounts = arrayOf(
        Account("0", 1000.00),
        Account("1", 2000.00),
        Account("2", 3000.00),
        Account("3", 4000.10),
    )

    fun fromByteArray(byteArray: ByteArray) {
//        val map = parse(byteArray)
//        accounts = map.accounts
    }

    fun toByteArray(): ByteArray {
        return ByteArray(10)
//        return MapOperations.mapToByteArray(
//            mapOf("accounts" to serialize(accounts))
//        )
    }
}