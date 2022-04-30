package cz.abo.b2b.web.importer.impl

import cz.abo.b2b.web.SystemUtils.Companion.usedMemory
import cz.abo.b2b.web.importer.dto.ImportSource
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.w3c.dom.Element
import java.io.*
import java.math.BigDecimal
import java.math.RoundingMode
import javax.xml.parsers.DocumentBuilderFactory


abstract class AbstractXMLParser {
    companion object {
        internal val LOGGER = LoggerFactory.getLogger(HeurekaXMLParser::class.java)
    }

    fun parseToXMLDocument(importSource: ImportSource): Element {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        LOGGER.info("XXX BEFORE READ: " + importSource.path + "; " + usedMemory())

        var xmlAsString = IOUtils.toString(importSource.newInputStream(), "UTF-8")
        xmlAsString = xmlAsString.replace("&nbsp;", "&#160;")

        val document = builder.parse(ByteArrayInputStream(xmlAsString.toByteArray()))
        document.documentElement.normalize()
        LOGGER.info("XXX AFTER READ: " + importSource.path + "; " + usedMemory())
        val root = document.documentElement
        return root
    }

    protected fun vatToNoVat(priceVAT: BigDecimal?, vat: Double) =
        priceVAT!!.divide(BigDecimal(1 + vat), 5, RoundingMode.HALF_UP).stripTrailingZeros()

}
