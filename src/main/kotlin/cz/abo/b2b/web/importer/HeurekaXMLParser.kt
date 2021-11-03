package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Product
import org.springframework.stereotype.Component
import java.io.File
import java.math.BigDecimal
import javax.xml.parsers.DocumentBuilderFactory

@Component
class HeurekaXMLParser {

    @Throws(Exception::class)
    fun products(): List<Product>? {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val file = HeurekaXMLParser::class.java.getResource("/xml/example.xml").file
        val document = builder.parse(File(file))
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
                    priceVAT = BigDecimal(java.lang.Double.valueOf(priceVatStr))
                }
            }
            try {
                val product = Product(productName!!, priceVAT!!, description)
                result.add(product)
            } catch (e: Exception) {
                //TODO email problem with importing product
                println(e)
            }
        }
        return result
    }
}