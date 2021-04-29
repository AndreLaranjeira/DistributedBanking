package banking

data class Account (
    val id: String,
    val password: String,
    var value: Double
)

data class AccountInformation(
    val id: String,
    val value: Double,
    val transferTariff: Double,
)