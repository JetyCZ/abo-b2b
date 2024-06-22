package cz.abo.b2b.web.bank
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import cz.abo.b2b.web.bank.dto.Root
import cz.abo.b2b.web.bank.dto.Transaction

class BankResponseParser {
    private val mapper = ObjectMapper()
    fun parse(json: String): List<Transaction>? {
        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        val (lastPage, transactions) = objectMapper.readValue(
            json,
            Root::class.java
        )
        return transactions
    }

}
