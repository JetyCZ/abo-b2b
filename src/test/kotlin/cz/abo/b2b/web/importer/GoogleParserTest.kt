package cz.abo.b2b.web.importer

import cz.abo.b2b.web.TestUtils
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.impl.GoogleXMLParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.math.BigDecimal

internal class GoogleParserTest {

    val googleXMLParser: GoogleXMLParser = GoogleXMLParser()

    @Test
    fun parseStream() {
        val file = GoogleXMLParser::class.java.getResource("/xml/google-example.xml").file
        val input = File(file)

        val supplier = Supplier(
            "Test",
            BigDecimal(1000),
            ArrayList(),"",
            "https://www.probio.cz/data/product-feed/probio/8re6tf8erd5ordd23c7f59a63.xml",
            "",
            ""
        )
        val products = googleXMLParser.parseStream(ImportSource.fromFile(input.absolutePath), supplier)
        val product1 = products.get(0)
        assertEquals("Itumbe Victoria Green", product1.productName)
        assertEquals(0.15, product1.VAT, 0.1)
        TestUtils.assertBigDecimal(1000/1.15, product1.priceNoVAT)
    }
}
