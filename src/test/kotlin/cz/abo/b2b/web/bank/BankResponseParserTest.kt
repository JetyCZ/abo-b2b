package cz.abo.b2b.web.bank

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertNotNull


open class BankResponseParserTest {
    @Test
    fun test() {
        val parser = BankResponseParser()
        val jsonPath = javaClass.getResource("/bank/transactions.json").file
        val json = FileUtils.readFileToString(File(jsonPath), "UTF-8")
        val parsed = parser.parse(json);
        assertNotNull(parsed)
    }
}
