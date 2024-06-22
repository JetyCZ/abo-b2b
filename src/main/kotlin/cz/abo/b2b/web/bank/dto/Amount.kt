package cz.abo.b2b.web.bank.dto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


// A class that represents an amount object
data class Amount @JsonCreator constructor(
    @JsonProperty("value") var value: Double, @JsonProperty("currency") var currency: String?
)
