package cz.abo.b2b.web.importer.impl

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.dao.UnitEnum
import cz.abo.b2b.web.importer.dto.ImportSource
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

@Component
class HeurekaXMLParser : AbstractXMLParser() {

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 2024-02-24
    val productNamePattern = Pattern.compile("^.*?(?<quantity>[\\s\\d+\\.\\,]+)\\skg(\\s|$).*")

    fun parseStream(importSource: ImportSource, supplier: Supplier): MutableList<Product> {
        val root = parseToXMLDocument(importSource)
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
                for (j in 0 until shopItemChildrenCount) {
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
                                quantity = matcher.group("quantity")
                                    .replace(",", ".")
                                    .replace(" ", "")
                                    .toBigDecimal()
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
                        priceVatStr = priceVatStr.replace("\\s".toRegex(), "").replace(',','.')
                        priceVAT = BigDecimal(priceVatStr.toDouble())
                    } else if ("VAT" == nodeName) {
                        var vatStr = shopItemChild.firstChild.nodeValue
                        vat = vatStr.replace("%", "").toDouble() * 0.01
                    } else if ("EAN" == nodeName) {
                        if (shopItemChild.firstChild!=null) {
                            ean = shopItemChild.firstChild.nodeValue
                        }
                    }
                }
                if (unit.equals(UnitEnum.KG) && quantity.compareTo(BigDecimal.ONE)<0) {
                    continue;
                }
                var priceNoVAT = vatToNoVat(priceVAT, vat)
                if (unit.equals(UnitEnum.KG) && quantity.compareTo(BigDecimal.ONE)>0) {
                    priceNoVAT = priceNoVAT.divide(quantity, 5, RoundingMode.HALF_UP).stripTrailingZeros();
                }
                val product = Product(productName, priceNoVAT, vat, description, quantity, unit, ean, supplier)
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
