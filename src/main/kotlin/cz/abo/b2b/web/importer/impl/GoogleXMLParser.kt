package cz.abo.b2b.web.importer.impl

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.dao.UnitEnum
import cz.abo.b2b.web.importer.dto.ImportSource
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class GoogleXMLParser : AbstractXMLParser() {

    fun parseStream(importSource: ImportSource, supplier: Supplier): MutableList<Product> {
        val root = parseToXMLDocument(importSource)
        val result: MutableList<Product> = ArrayList()
        val entries = root.getElementsByTagName("entry")
        for (i in 0 until entries.length) {
            try {
                val entry = entries.item(i)
                val entryChildren = entry.childNodes
                val entryChildrenCount = entryChildren.length
                var productName = "Neznámý produkt"
                var description: String? = null
                var priceVAT: BigDecimal? = null
                var quantity = BigDecimal.ONE
                var vat = 0.15
                var unit = UnitEnum.KS
                var id : String? = null
                for (j in 1 until entryChildrenCount) {
                    val shopItemChild = entryChildren.item(j)
                    if (shopItemChild.firstChild==null || StringUtils.isEmpty(shopItemChild.firstChild.nodeValue)) {
                        continue;
                    }
                    val nodeName = shopItemChild.nodeName
                    if ("title" == nodeName) {
                        productName = shopItemChild.firstChild.nodeValue
                    } else if ("summary" == nodeName) {
                        description = shopItemChild.firstChild.nodeValue
                    } else if ("g:price" == nodeName) {
                        var priceVatStr = shopItemChild.firstChild.nodeValue
                        priceVatStr = priceVatStr.replace(',', '.')
                        priceVatStr = priceVatStr.replace(" CZK", "")
                        priceVAT = BigDecimal(priceVatStr.toDouble())
                    } else if ("g:id" == nodeName) {
                        if (shopItemChild.firstChild!=null) {
                            id = shopItemChild.firstChild.nodeValue
                        }
                    }
                }
                val priceNoVAT = vatToNoVat(priceVAT, vat)
                val product = Product(productName, priceNoVAT, vat, description, quantity, unit, null, supplier)
                product.supplierCode = id
                result.add(product)
            } catch (e: Exception) {
                //TODO email problem with importing product
                println(e)
            }
        }
        return result
    }

}