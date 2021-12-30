package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.xls.processor.BionebioSheetProcessor
import cz.abo.b2b.web.importer.xls.processor.KServisSheetProcessor
import cz.abo.b2b.web.importer.xls.processor.NutSheetProcessor
import cz.abo.b2b.web.importer.xls.processor.WolfberrySheetProcessor
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class Suppliers {

    @Throws(Exception::class)
    fun suppliers(): List<Supplier> {
        var result = ArrayList<Supplier>()

        /*result.add(
            Supplier("PROBIO", BigDecimal(2500), "", "https://www.probio.cz/data/product-feed/probio/8re6tf8erd5ordd23c7f59a63.xml", "")
        )
*/
        result.add(
            Supplier("Wolfberry", BigDecimal(2000), "", "http://cup.wolfberry.cz/xml-export/bezobalu_cz.xml",
                WolfberrySheetProcessor::class.qualifiedName!!, "objednavky@wolfberry.cz")
        )
        var importerClassName = BionebioSheetProcessor::class.qualifiedName
        if (importerClassName!=null) {
            result.add(
                Supplier(
                    "bio nebio",
                    BigDecimal(0.85 * 3000),
                    "",
                    "/bionebio/OL_bio_nebio_11_2021.xls",
                    importerClassName,
                    "objednavky@bionebio.cz"
                )
            )
        }

        if (importerClassName!=null) {
            result.add(
                Supplier("K-servis", BigDecimal(5000), "", "/k-servis/cenik_srpen.xlsx", KServisSheetProcessor::class.qualifiedName!!, "k-servis@k-servis.com")
            )
        }

        importerClassName = NutSheetProcessor::class.qualifiedName
        if (importerClassName!=null) {
            result.add(
                Supplier("Oříšek", BigDecimal(5000), "", "/orisek/orisek_01.10.2021.xls", importerClassName, "orisek@orisek.cz")
            )
        }
        // TODO remove - just for testing
        result.forEach{r->r.orderEmail = StringUtils.replace(r.orderEmail, "@","AT") + "@mailinator.com"}
        return result
    }
}