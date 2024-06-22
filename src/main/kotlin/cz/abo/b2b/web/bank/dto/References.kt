package cz.abo.b2b.web.bank.dto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


// A class that represents a references object
class References  // A default constructor

// A class that represents a related parties object
data class RelatedParties @JsonCreator constructor(@JsonProperty("counterParty") private var counterParty: CounterParty?)
