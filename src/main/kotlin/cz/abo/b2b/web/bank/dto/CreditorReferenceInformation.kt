package cz.abo.b2b.web.bank.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class CreditorReferenceInformation @JsonCreator constructor(
    @JsonProperty("variable") private val variable: String?,
    @JsonProperty("constant") private val constant: String?,
    @JsonProperty("specific") private val specific: String?
)
