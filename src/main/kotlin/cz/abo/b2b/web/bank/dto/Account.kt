package cz.abo.b2b.web.bank.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Account @JsonCreator constructor(
    @JsonProperty("accountNumber") var accountNumber: String
)
