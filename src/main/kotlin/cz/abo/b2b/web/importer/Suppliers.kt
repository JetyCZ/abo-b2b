package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class Suppliers {

    @Throws(Exception::class)
    fun suppliers(): List<Supplier> {
        var probio = Supplier("PROBIO", BigDecimal(2500), "", "https://www.probio.cz/data/product-feed/probio/8re6tf8erd5ordd23c7f59a63.xml")
        return listOf(probio)
    }
}