package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import org.springframework.stereotype.Component
import java.io.File
import java.math.BigDecimal
import javax.xml.parsers.DocumentBuilderFactory

@Component
class HeurekaXMLParser() {

    fun parseStream(file: File, supplier: Supplier): MutableList<Product> {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        val document = builder.parse(file)
        document.documentElement.normalize()
        val root = document.documentElement
        val result: MutableList<Product> = ArrayList()
        val shopitems = root.getElementsByTagName("SHOPITEM")
        for (i in 0 until shopitems.length) {
            val shopItem = shopitems.item(i)
            val shopItemChildren = shopItem.childNodes
            val shopItemChildrenCount = shopItemChildren.length
            var productName: String? = null
            var description: String? = null
            var priceVAT: BigDecimal? = null
            var vat = 0.15
            for (j in 1 until shopItemChildrenCount) {
                val shopItemChild = shopItemChildren.item(j)
                val nodeName = shopItemChild.nodeName
                if ("PRODUCTNAME" == nodeName) {
                    productName = shopItemChild.firstChild.nodeValue
                } else if ("DESCRIPTION" == nodeName) {
                    description = shopItemChild.firstChild.nodeValue
                } else if ("PRICE_VAT" == nodeName) {
                    var priceVatStr = shopItemChild.firstChild.nodeValue
                    priceVatStr = priceVatStr.replace(',', '.')
                    priceVAT = BigDecimal(priceVatStr.toDouble())
                } else if ("VAT" == nodeName) {
                    var vatStr = shopItemChild.firstChild.nodeValue
                    vat = vatStr.replace("%","").toDouble() * 0.01
                }
            }
            try {
                val product = Product(productName!!, priceVAT!!, vat, description, BigDecimal.ONE, supplier)
                result.add(product)
            } catch (e: Exception) {
                //TODO email problem with importing product
                println(e)
            }
        }
        return result
    }
}