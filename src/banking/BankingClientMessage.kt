package banking

// Imports.
import com.google.gson.Gson

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
        return Gson().toJson(this).toByteArray()
    }

    companion object {
        fun fromByteArray(byteArray: ByteArray): BankingClientMessage {
            return Gson().fromJson(String(byteArray), BankingClientMessage::class.java)
        }
    }
}