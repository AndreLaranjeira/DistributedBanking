package banking

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT

data class LoginData(
    val token: String,
    val id: String,
    val value: Double
)

class Auth {
    /**
     * Examples:
     *   try {
     *       val loginData = Auth.login("1", "batata1")
     *       println(loginData.token)
     *   } catch (e: NoSuchElementException) {
     *       println("Invalid account or password")
     *   }
     *
     *   try {
     *       val loginData = Auth.login("1", "batata1")
     *       val account = Auth.validate(loginData.token)
     *       println(account.value)
     *   } catch (e: NoSuchElementException) {
     *       println("Invalid Token")
     *   }
     */

    companion object {
        fun login(accountId: String, password: String) : LoginData{
            val accounts = ServerState.internalState.accounts
            val account = accounts.single { x -> (x.id == accountId && x.password == password) }
            val token = encode(account.id)

            // On error throw NoSuchElementException
            return LoginData(token, account.id, account.value)
        }

        fun validate(token: String) : Account{
            val accountId = decode(token)
            val accounts = ServerState.internalState.accounts
            println(accountId)

            // On error throw NoSuchElementException
            return accounts.single { it.id == accountId }
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
                .build() //Reusable verifier instance

            val jwt: DecodedJWT = verifier.verify(token)
            return jwt.getClaim("accountId").asString()
        }
    }
}