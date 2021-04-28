package banking

// Imports.
import com.google.gson.*
import java.lang.reflect.Type

// Enum Classes.
enum class AuthOperationsCode {
    CREATE, ACCESS,
}
enum class BankingOperationsCode {
    DEPOSIT, WITHDRAW, TRANSFER, PIX,
}

enum class ClientMessageType {
    AUTH, BANKING,
}

// Classes.
open class ClientMessage(val messageType: ClientMessageType) {

    data class AuthMessage(
            val operationCode: AuthOperationsCode,
            val accountId: String?,
            val accountPassword: String,
    ): ClientMessage(ClientMessageType.AUTH)

    data class BankingMessage(
            val operationCode: BankingOperationsCode,
            val originAccount: String,
            val targetAccount: String?,
            val operationValue: Double,
            val jwtToken: String?
    ): ClientMessage(ClientMessageType.BANKING)

    fun toByteArray() : ByteArray {
        return clientMessageGSON.toJson(this).toByteArray()
    }

    companion object {
        private val clientMessageGSON : Gson

        fun fromByteArray(byteArray: ByteArray): ClientMessage {
            return clientMessageGSON.fromJson(String(byteArray), ClientMessage::class.java)
        }

        init {
            val clientMessageGsonBuilder = GsonBuilder()
            clientMessageGsonBuilder.registerTypeAdapter(ClientMessage::class.java, ClientMessageAdapter())
            clientMessageGSON = clientMessageGsonBuilder.create()
        }
    }
}

class ClientMessageAdapter : JsonSerializer<ClientMessage>, JsonDeserializer<ClientMessage> {

    override fun serialize(src: ClientMessage, typeOfSrc: Type?, context: JsonSerializationContext): JsonElement {
        return context.serialize(src, src.javaClass)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): ClientMessage {
        val jsonObject = json.asJsonObject
        val messageType = ClientMessageType.valueOf(jsonObject["messageType"].asString)
        return try {
            when(messageType) {
                ClientMessageType.AUTH -> context.deserialize(jsonObject, ClientMessage.AuthMessage::class.java)
                ClientMessageType.BANKING -> context.deserialize(jsonObject, ClientMessage.BankingMessage::class.java)
            }
        } catch (e: Exception) {
            throw JsonParseException("An unexpected error occurred when trying to ")
        }
    }
}
