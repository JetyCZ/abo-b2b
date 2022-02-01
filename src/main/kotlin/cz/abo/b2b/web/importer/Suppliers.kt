package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.xls.processor.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class Suppliers {

    @Throws(Exception::class)
    fun suppliers(): List<Supplier> {
        var result = ArrayList<Supplier>()

        result.addAll(

            listOf(
                Supplier("Diana", freeTransportFromVatValue(6000),
                    "Nejširší výběr sušeného a lyofilizovaného ovoce, ořechů," +
                            " super-potravin, semínek, sušeného ovoce a ořechů v polevách, " +
                            "ale i cukrovinek, želé a lékořice doručíme nyní až k Vám domů! " +
                            "Prioritou je nejvyšší kvalita produktů, které dovážíme přímo od farmářů, výrobců či zpracovatelů výhradně ze zemí původu. " +
                            "Náš sortiment jsme pro Vás doplnili o pečlivě vybrané spektrum zdravých potravin, " +
                            "mezi kterými najdete ořechové pasty a másla, bezlepkové pečivo, obiloviny, vločky, kaše, marmelády a džemy, " +
                            "tyčinky, sirupy, kávu a čaje, ale také například luštěniny nebo sušené houby, " +
                            "které splňují naše představy o nejvyšší kvalitě.",
                    "https://www.diana-company.cz/user/documents/9qxpt15n-s87hlpvk-a.xml",
                    DianaSheetProcessor::class.qualifiedName!!,
                    "diana.company@diana-company.cz"),

                Supplier("PROBIO", BigDecimal(2500), "", "https://www.probio.cz/data/product-feed/probio/8re6tf8erd5ordd23c7f59a63.xml",
                    ProbioSheetProcessor::class.qualifiedName!!, "objednavky@probio.cz"),

                Supplier("Wolfberry", BigDecimal(2000), "", "http://cup.wolfberry.cz/xml-export/bezobalu_cz.xml",
                    WolfberrySheetProcessor::class.qualifiedName!!, "objednavky@wolfberry.cz"),

               Supplier("Sičaj",
                   freeTransportFromVatValue(5000), "",
                   "http://www.sicajbezobalu.cz/google.xml",
                    SicajSheetProcessor::class.qualifiedName!!, "petr.sic@post.cz"),
                Supplier(
                    "bio nebio",
                    BigDecimal(0.85 * 3000),
                    "",
                    "/bionebio/OL_bio_nebio_12_2021.xls",
                    BionebioSheetProcessor::class.qualifiedName!!,
                    "objednavky@bionebio.cz"
                ),
                Supplier("K-servis", BigDecimal(5000), "", "/k-servis/cenik_srpen.xlsx", KServisSheetProcessor::class.qualifiedName!!, "k-servis@k-servis.com"),
                Supplier("Oříšek", BigDecimal(5000), "", "/orisek/orisek_01.10.2021.xls", NutSheetProcessor::class.qualifiedName!!, "orisek@orisek.cz")

         )

        )

        return result
    }

    private fun freeTransportFromVatValue(freeTransportIncludingVAT: Int) = BigDecimal(freeTransportIncludingVAT).divide(BigDecimal(1.15), 5, RoundingMode.HALF_UP)
}