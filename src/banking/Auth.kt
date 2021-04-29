package banking

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.interfaces.DecodedJWT

data class LoginData(
    val token: String,
    val id: String,
    val value: Double,
    val tax: Double,
)

object Auth {
    /**
     * Examples:
     *   try {
     *       val loginData = Auth.login("1", "batata1")
     *       println(loginData.token)
     *   } catch (e: Exception) {
     *       println("Invalid account or password")
     *   }
     *
     *   try {
     *       val loginData = Auth.login("1", "batata1")
     *       val account = Auth.validate(loginData.token)
     *       println(account.value)
     *   } catch (e: Exception) {
     *       println("Invalid Token")
     *   }
     */

    fun login(accountId: String, password: String) : LoginData{
        val accounts = ServerState.internalState.accounts
        val account = accounts.singleOrNull { x -> (x.id == accountId && x.password == password) } ?:
            throw NoSuchElementException("Invalid login credentials! Please try again.")

        val token = encode(account.id)

        return LoginData(token, account.id, account.value, ServerState.internalState.transferTariff)
    }

    fun validate(token: String) : AccountInformation{
        try {
            val accountId = decode(token)
            val accounts = ServerState.internalState.accounts
            val transferTariff = ServerState.internalState.transferTariff

            val account = accounts.singleOrNull { it.id == accountId } ?:
                throw NoSuchElementException("Account does not exist anymore!")

            return AccountInformation(account.id, account.value, transferTariff)
        }
        catch (e: Exception) {
            when(e) {
                is JWTDecodeException -> throw JWTDecodeException("Invalid token!")
                else -> throw e
            }
        }
    }

    private fun encode(accountId: String) : String{
        val algorithmHS = Algorithm.HMAC256("secret")
        val token = JWT.create()
            .withClaim("accountId", accountId)
            .withIssuer("auth0")
            .sign(algorithmHS)
        return token.toString()
    }

    private fun decode(token: String) : String {
        val algorithmHS = Algorithm.HMAC256("secret")
        val verifier: JWTVerifier = JWT.require(algorithmHS)
            .withIssuer("auth0")
            .build() // Reusable verifier instance.

        val jwt: DecodedJWT = verifier.verify(token)
        return jwt.getClaim("accountId").asString()
    }
}