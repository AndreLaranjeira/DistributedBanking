// Package.
package banking

// Imports.
import com.google.gson.Gson

// Classes.
enum class ServerMessageResultCode {
    AUTHENTICATION_ERROR, SUCCESS
}

data class ServerMessage(val resultCode: ServerMessageResultCode) {

    fun toByteArray() : ByteArray {
        return Gson().toJson(this).toByteArray()
    }

    companion object {
        fun fromByteArray(byteArray: ByteArray): ServerMessage {
            return Gson().fromJson(String(byteArray), ServerMessage::class.java)
        }
    }
}