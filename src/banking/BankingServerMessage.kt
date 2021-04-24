// Package.
package banking

// Imports.
import com.google.gson.Gson

// Classes.
enum class BankingServerMessageResultCode {
    AUTHENTICATION_ERROR, SUCCESS
}

data class BankingServerMessage(val resultCode: BankingServerMessageResultCode) {

    fun toByteArray() : ByteArray {
        return Gson().toJson(this).toByteArray()
    }

    companion object {
        fun fromByteArray(byteArray: ByteArray): BankingServerMessage {
            return Gson().fromJson(String(byteArray), BankingServerMessage::class.java)
        }
    }
}