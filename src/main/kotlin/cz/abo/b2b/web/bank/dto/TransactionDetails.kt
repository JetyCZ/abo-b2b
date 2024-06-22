package cz.abo.b2b.web.bank.dto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import cz.abo.b2b.web.bank.dto.RemittanceInformation


// A class that represents a transaction details object
data class TransactionDetails @JsonCreator constructor(
    @JsonProperty("references") private var references: References?,
    @JsonProperty("relatedParties") private var relatedParties: RelatedParties?,
    @JsonProperty("remittanceInformation") private var remittanceInformation: RemittanceInformation?
)
