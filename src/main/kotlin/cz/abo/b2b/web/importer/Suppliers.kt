package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.xls.processor.BionebioSheetProcessor
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class Suppliers {

    @Throws(Exception::class)
    fun suppliers(): List<Supplier> {
        var result = ArrayList<Supplier>()

        result.add(
            Supplier("PROBIO", BigDecimal(2500), "", "https://www.probio.cz/data/product-feed/probio/8re6tf8erd5ordd23c7f59a63.xml", "")
        )

        val importerClassName = BionebioSheetProcessor::class.qualifiedName
        if (importerClassName!=null) {
            result.add(
                Supplier("bio nebio", BigDecimal(0.85 * 3000), "", "/bionebio/OL_bio nebio_11_2021.xls", importerClassName)
            )
        }

        return result
    }
}