// Package.
package banking

// Imports.
import operations.MapOperations

// Classes.
enum class BankingServerMessageResultCode {
    AUTHENTICATION_ERROR, SUCCESS
}

data class BankingServerMessage(val resultCode: BankingServerMessageResultCode) {

    fun toByteArray() : ByteArray {
        return MapOperations.mapToByteArray(
            mapOf("resultCode" to resultCode)
        )
    }

    constructor(byteArray: ByteArray) : this(
        BankingServerMessageResultCode.valueOf(MapOperations.byteArrayToMap(byteArray).getValue("resultCode"))
    )
}