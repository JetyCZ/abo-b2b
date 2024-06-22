package cz.abo.b2b.web.bank.dto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


// A class that represents an entry details object
data class EntryDetails// A constructor with @JsonCreator and @JsonProperty annotations
@JsonCreator constructor(@JsonProperty("transactionDetails") private var transactionDetails: TransactionDetails?)
