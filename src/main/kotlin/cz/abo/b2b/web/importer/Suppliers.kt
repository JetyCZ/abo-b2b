package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.xls.processor.BionebioSheetProcessor
import cz.abo.b2b.web.importer.xls.processor.KServisSheetProcessor
import cz.abo.b2b.web.importer.xls.processor.NutSheetProcessor
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

        importerClassName = KServisSheetProcessor::class.qualifiedName
        if (importerClassName!=null) {
            result.add(
                Supplier("K-servis", BigDecimal(5000), "", "/k-servis/cenik_srpen.xlsx", importerClassName, "k-servis@k-servis.com")
            )
        }

        importerClassName = NutSheetProcessor::class.qualifiedName
        if (importerClassName!=null) {
            result.add(
                Supplier("Oříšek", BigDecimal(5000), "", "/orisek/orisek_01.10.2021.xls", importerClassName, "orisek@orisek.cz")
            )
        }

        return result
    }
}