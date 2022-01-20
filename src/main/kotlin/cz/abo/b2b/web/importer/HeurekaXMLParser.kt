package cz.abo.b2b.web.importer

import cz.abo.b2b.web.SystemUtils
import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.dao.UnitEnum
import cz.abo.b2b.web.importer.dto.ImportSource
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory

@Component
class HeurekaXMLParser {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(HeurekaXMLParser::class.java)
    }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 2024-02-24
    val productNamePattern = Pattern.compile("^.*(?<quantity>\\d+)\\skg(\\s|$).*")

    fun parseStream(importSource: ImportSource, supplier: Supplier): MutableList<Product> {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        LOGGER.info("XXX BEFORE READ: " + importSource.path + "; " + SystemUtils.usedMemory())
        val document = builder.parse(importSource.newInputStream())
        document.documentElement.normalize()
        LOGGER.info("XXX AFTER READ: " + importSource.path + "; " + SystemUtils.usedMemory())
        val root = document.documentElement
        val result: MutableList<Product> = ArrayList()
        val shopitems = root.getElementsByTagName("SHOPITEM")
        for (i in 0 until shopitems.length) {
            try {
                val shopItem = shopitems.item(i)
                val shopItemChildren = shopItem.childNodes
                val shopItemChildrenCount = shopItemChildren.length
                var productName = "Neznámý produkt"
                var description: String? = null
                var priceVAT: BigDecimal? = null
                var quantity = BigDecimal.ONE
                var bestBefore: LocalDate? = null
                var vat = 0.15
                var ean: String? = null
                var unit = UnitEnum.KS
                for (j in 1 until shopItemChildrenCount) {
                    val shopItemChild = shopItemChildren.item(j)
                    if (shopItemChild.firstChild==null || StringUtils.isEmpty(shopItemChild.firstChild.nodeValue)) {
                        continue;
                    }
                    val nodeName = shopItemChild.nodeName
                    if ("PRODUCTNAME" == nodeName) {
                        productName = shopItemChild.firstChild.nodeValue
                        try {
                            val matcher = productNamePattern.matcher(productName)
                            if (matcher.matches()) {
                                quantity = matcher.group("quantity").toBigDecimal()
                                unit = UnitEnum.KG
                            }
                        } catch (e: Exception) {
                            LOGGER.warn("Error calculating quantity from product name: " + productName)
                        }

                    } else if ("DESCRIPTION" == nodeName) {
                        description = shopItemChild.firstChild.nodeValue
                    } else if ("BESTBEFORE" == nodeName) {
                        val bestBeforeStr = shopItemChild.firstChild.nodeValue
                        if (bestBeforeStr != null) {
                            bestBefore = LocalDate.parse(bestBeforeStr, dateFormatter)
                        }
                    } else if ("PRICE_VAT" == nodeName) {
                        var priceVatStr = shopItemChild.firstChild.nodeValue
                        priceVatStr = priceVatStr.replace(',', '.')
                        priceVAT = BigDecimal(priceVatStr.toDouble())
                    } else if ("VAT" == nodeName) {
                        var vatStr = shopItemChild.firstChild.nodeValue
                        if (vat != null) {
                            vat = vatStr.replace("%", "").toDouble() * 0.01
                        }
                    } else if ("EAN" == nodeName) {
                        if (shopItemChild.firstChild!=null) {
                            ean = shopItemChild.firstChild.nodeValue
                        }
                    }
                }
                if (quantity==null) quantity = BigDecimal.ONE
                val product = Product(productName, priceVAT!!, vat, description, quantity, unit, ean, supplier)
                product.bestBefore = bestBefore
                result.add(product)
            } catch (e: Exception) {
                //TODO email problem with importing product
                println(e)
            }
        }
        return result
    }
}