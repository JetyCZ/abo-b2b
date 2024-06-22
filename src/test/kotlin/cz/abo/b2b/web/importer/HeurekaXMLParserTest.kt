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
    val supplier = Supplier(
        "Test",
        BigDecimal(1000),
        ArrayList(),"",
        "https://www.probio.cz/data/product-feed/probio/8re6tf8erd5ordd23c7f59a63.xml",
        "",
        ""
    )

    @Test
    fun parseStreamProbio() {
        val file = HeurekaXMLParser::class.java.getResource("/xml/probioExample.xml").file
        val input = File(file)


        val products = heurekaXMLParser.parseStream(ImportSource.fromFile(input.absolutePath), supplier)
        val product1 = products.get(0)
        assertEquals(0.15, product1.VAT, 0.1)
    }

    @Test
    fun parseStreamSvetPlodu() {
        val file = HeurekaXMLParser::class.java.getResource("/xml/svetPloduExample.xml").file
        val input = File(file)
        val products = heurekaXMLParser.parseStream(ImportSource.fromFile(input.absolutePath), supplier)
        val product1 = products.get(0)
        assertEquals("X", product1.productName)
    }
}
