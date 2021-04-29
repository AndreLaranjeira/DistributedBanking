// Package.
package banking

// Imports.
import com.google.gson.*
import java.lang.reflect.Type

// Enum Classes.
enum class ServerMessageType {
    ACCOUNT_CREATION, LOGIN, BANKING, NONE
}

enum class ServerMessageResultCode {
    SUCCESS, ERROR
}


open class ServerMessage(
        val messageType: ServerMessageType,
        val resultCode: ServerMessageResultCode,
        val errorMessage: String,
        ) {

    class AccountCreationMessage(
            resultCode: ServerMessageResultCode,
            errorMessage: String,
            val accountId: String?
    ): ServerMessage(ServerMessageType.ACCOUNT_CREATION, resultCode, errorMessage)

    class LoginMessage(
            resultCode: ServerMessageResultCode,
            errorMessage: String,
            val loginData: LoginData?
    ): ServerMessage(ServerMessageType.LOGIN, resultCode, errorMessage)

    class BankingMessage(
            resultCode: ServerMessageResultCode,
            errorMessage: String,
            val value: Double?
    ): ServerMessage(ServerMessageType.BANKING, resultCode, errorMessage)

    fun toByteArray() : ByteArray {
        return serverMessageGSON.toJson(this).toByteArray()
    }

    companion object {
        private val serverMessageGSON : Gson

        fun fromByteArray(byteArray: ByteArray): ServerMessage {
            return serverMessageGSON.fromJson(String(byteArray), ServerMessage::class.java)
        }

        init {
            val serverMessageGsonBuilder = GsonBuilder()
            serverMessageGsonBuilder.registerTypeAdapter(ServerMessage::class.java, ServerMessageAdapter())
            serverMessageGSON = serverMessageGsonBuilder.create()
        }
    }
}

class ServerMessageAdapter : JsonSerializer<ServerMessage>, JsonDeserializer<ServerMessage> {

    override fun serialize(src: ServerMessage, typeOfSrc: Type?, context: JsonSerializationContext): JsonElement {
        return context.serialize(src, src.javaClass)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): ServerMessage {
        val jsonObject = json.asJsonObject
        val messageType = ServerMessageType.valueOf(jsonObject["messageType"].asString)
        return try {
            when(messageType) {
                ServerMessageType.ACCOUNT_CREATION -> context.deserialize(jsonObject, ServerMessage.AccountCreationMessage::class.java)
                ServerMessageType.LOGIN -> context.deserialize(jsonObject, ServerMessage.LoginMessage::class.java)
                ServerMessageType.BANKING -> context.deserialize(jsonObject, ServerMessage.BankingMessage::class.java)
                ServerMessageType.NONE -> context.deserialize(jsonObject, ServerMessage::class.java)
            }
        } catch (e: Exception) {
            throw JsonParseException("An unexpected error occurred when trying to ")
        }
    }
}