package banking

// Imports.
import com.google.gson.Gson

// Enum Classes.
enum class AuthOperationsCode {
    CREATE, ACCESS,
}
enum class BankingOperationsCode {
    DEPOSIT, WITHDRAW, TRANSFER, PIX,
}

open class ClientMessage() {
    data class AuthMessage(
            val operationCode: AuthOperationsCode,
            val accountId: String?,
            val accountPassword: String,
    ): ClientMessage() {

        fun toByteArray() : ByteArray {
            return Gson().toJson(this).toByteArray()
        }

        companion object {
            fun fromByteArray(byteArray: ByteArray): AuthMessage {
                return Gson().fromJson(String(byteArray), AuthMessage::class.java)
            }
        }
    }

    data class BankingMessage(
            val operationCode: BankingOperationsCode,
            val originAccount: String,
            val targetAccount: String?,
            val operationValue: Double,
            val jwtToken: String?
    ): ClientMessage() {

        fun toByteArray() : ByteArray {
            return Gson().toJson(this).toByteArray()
        }

        companion object {
            fun fromByteArray(byteArray: ByteArray): BankingMessage {
                return Gson().fromJson(String(byteArray), BankingMessage::class.java)
            }
        }
    }
}

