package banking

object Teste {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            val loginData = Auth.login("1", "batata1")
            println(loginData.token)
        } catch (e: NoSuchElementException) {
            println("Invalid account or password")
        }

        try {
            val loginData = Auth.login("1", "batata1")
            val account = Auth.validate(loginData.token)
            println(account.value)
        } catch (e: NoSuchElementException) {
            println("Invalid Token")
        }
    }
}