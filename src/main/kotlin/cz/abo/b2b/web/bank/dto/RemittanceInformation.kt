package cz.abo.b2b.web.bank.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class RemittanceInformation @JsonCreator constructor(
    @JsonProperty("creditorReferenceInformation") private val creditorReferenceInformation: CreditorReferenceInformation?,
    @JsonProperty("originatorMessage") private val originatorMessage: String?,
    @JsonProperty("unstructured") private val unstructured: String?
)

