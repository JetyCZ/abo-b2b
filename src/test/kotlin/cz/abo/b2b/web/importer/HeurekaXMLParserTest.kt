package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
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

        val supplier = Supplier("Test", BigDecimal(1000), "", null)
        val products = heurekaXMLParser.parseStream(input, supplier)
        val product1 = products.get(0)
        assertEquals(0.15, product1.VAT, 0.1)
    }
}