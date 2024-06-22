package cz.abo.b2b.web.bank.dto
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


data class BankTransactionCode @JsonCreator constructor(// Getters and setters
    @JsonProperty("code") var code: String?
) {

}
