package cz.abo.b2b.web.bank.dto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


// A class that represents a transaction object
data class Transaction @JsonCreator constructor(// Getters and setters
    /**
     * Unique identifier of given transaction
     */
    @JsonProperty("entryReference") var entryReference: String?,
    @JsonProperty("amount") var amount: Amount?,
    @JsonProperty("creditDebitIndication") var creditDebitIndication: String?,
    @JsonProperty("bookingDate") var bookingDate: String?,
    @JsonProperty("valueDate") var valueDate: String?,
    @JsonProperty("bankTransactionCode") var bankTransactionCode: BankTransactionCode?,
    @JsonProperty("entryDetails") private var entryDetails: EntryDetails?
)
