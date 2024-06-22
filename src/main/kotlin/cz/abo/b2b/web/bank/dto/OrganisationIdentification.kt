package cz.abo.b2b.web.bank.dto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


// A class that represents an organisation identification object
data class OrganisationIdentification @JsonCreator constructor(@JsonProperty("bankCode") var bankCode: String?)
