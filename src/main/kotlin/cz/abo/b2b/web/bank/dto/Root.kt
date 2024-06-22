package cz.abo.b2b.web.bank.dto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @link https://developers.rb.cz/premium/documentation/01rbczpremiumapi#/Get%20Transaction%20List/getTransactionList
 */
data class Root @JsonCreator constructor(
    @JsonProperty("lastPage") var lastPage: Boolean,
    @JsonProperty("transactions") var transactions: List<Transaction>?
)
