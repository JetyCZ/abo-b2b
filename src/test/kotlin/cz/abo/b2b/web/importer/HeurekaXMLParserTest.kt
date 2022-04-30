package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.impl.HeurekaXMLParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.math.BigDecimal

internal class HeurekaXMLParserTest {

    val heurekaXMLParser: HeurekaXMLParser = HeurekaXMLParser()

    @Test
    fun parseStream() {
        val file = HeurekaXMLParser::class.java.getResource("/xml/example.xml").file
        val input = File(file)

        val supplier = Supplier(
            "Test",
            BigDecimal(1000),
            "",
            "https://www.probio.cz/data/product-feed/probio/8re6tf8erd5ordd23c7f59a63.xml",
            "",
            ""
        )
        val products = heurekaXMLParser.parseStream(ImportSource.fromFile(input.absolutePath), supplier)
        val product1 = products.get(0)
        assertEquals(0.15, product1.VAT, 0.1)
    }
}