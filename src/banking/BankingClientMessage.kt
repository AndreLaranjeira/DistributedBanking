package banking

// Imports.
import operations.MapOperations

// Classes.
enum class BankingOperationsCode {
    DEPOSIT, WITHDRAW, TRANSFER, PIX,
}

data class BankingClientMessage(
        val operationCode: BankingOperationsCode,
        val originAccount: String,
        val targetAccount: String,
        val operationValue: Double
) {

    fun toByteArray() : ByteArray {
        return MapOperations.mapToByteArray(
                mapOf(
                        "operationCode" to operationCode,
                        "originAccount" to originAccount,
                        "targetAccount" to targetAccount,
                        "operationValue" to operationValue,
                )
        )
    }

    constructor(byteArray: ByteArray) : this(
        BankingOperationsCode.valueOf(MapOperations.byteArrayToMap(byteArray).getValue("operationCode")),
        MapOperations.byteArrayToMap(byteArray).getValue("originAccount"),
        MapOperations.byteArrayToMap(byteArray).getValue("targetAccount"),
        MapOperations.byteArrayToMap(byteArray).getValue("operationValue").toDouble(),
    )
}