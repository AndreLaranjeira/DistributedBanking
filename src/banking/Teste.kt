package banking

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

typealias listAccounts = List<Account>
val listOfMyClassObject = object : TypeToken<ArrayList<Account>>() {}.type

object Teste {
    data class state(
        var accounts: List<Account> = listOf(
            Account("0", "senha", 1000.00),
            Account("1", "senha", 2000.00),
            Account("2", "senha", 3000.00),
            Account("3", "senha", 4000.10),
        )
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val myState = state()

        val serial = myState.toString()
        val json = Gson().toJson(myState)
        val parsed = Gson().fromJson(json, state::class.java)


        println(serial)
        println(json)
        println(parsed.accounts[0])
    }
}