package cz.abo.b2b.web.bank.dto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


// A class that represents a counterparty object
data class CounterParty// A constructor with @JsonCreator and @JsonProperty annotations
@JsonCreator constructor(// Getters and setters
    @JsonProperty("name") var name: String?,
    @JsonProperty("organisationIdentification") private var organisationIdentification: OrganisationIdentification?,
    @JsonProperty("account") var account: Account?


)
