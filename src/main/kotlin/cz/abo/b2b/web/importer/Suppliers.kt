package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.xls.processor.*
import cz.abo.b2b.web.view.component.MathUtils
import cz.abo.b2b.web.view.component.MathUtils.Companion.withoutVAT
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
               // Supplier("Zelenina Rokytno", BigDecimal(500), ArrayList(),"", "1wp91zLLQBmJs_IpNXlVoSK3tAmGlJ7X3tFsA_TJPnj4",
               //     ProbioSheetProcessor::class.qualifiedName!!, "objednavky@probio.cz"),


                Supplier("Diana", freeTransportFromVatValue(6000),
                    ArrayList(),
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

                Supplier("PROBIO(ONLY THEIR EXCEL)", BigDecimal(2500), ArrayList(),"", "https://www.probio.cz/data/product-feed/probio/8re6tf8erd5ordd23c7f59a63.xml",
                    ProbioSheetProcessor::class.qualifiedName!!, "objednavky@probio.cz"),

                Supplier("Wolfberry", BigDecimal(2000), ArrayList(), "", "http://cup.wolfberry.cz/xml-export/bezobalu_cz.xml",
                    WolfberrySheetProcessor::class.qualifiedName!!, "objednavky@wolfberry.cz"),

               Supplier("Sičaj",
                   freeTransportFromVatValue(2000), ArrayList(), "",
                   "http://www.sicajbezobalu.cz/google.xml",
                    SicajSheetProcessor::class.qualifiedName!!, "petr.sic@post.cz"),
                Supplier(
                    "bio nebio (USE e-shop)",
                    freeTransportFromVatValue(4025),
                    ArrayList(),"",
                    "/bionebio/OL_bio nebio_08_2023.xlsx",
                    BionebioSheetProcessor::class.qualifiedName!!,
                    "objednavky@bionebio.cz"
                ),
                Supplier("K-servis", BigDecimal(5000), ArrayList(),"", "/k-servis/k-servis_cenik_cervenec2023.xlsx", KServisSheetProcessor::class.qualifiedName!!, "k-servis@k-servis.com"),
                Supplier("Oříšek", BigDecimal(5000),ArrayList(), "", "/orisek/orisek_07.2023.xlsx", NutSheetProcessor::class.qualifiedName!!, "orisek@orisek.cz"),

                // Labeta 1500 bez DPH > doprava zdarma

                Supplier(
                    "Caltha",
                    BigDecimal(4000),
                    ArrayList(),"",
                    "/caltha/2021-10_Objednavkovy_formular_CALTHA.xlsx",
                    CalthaSheetProcessor::class.qualifiedName!!,
                    "obchod@caltha.cz"
                ),

                Supplier("BIODVŮR Jaroslav Netík", BigDecimal.ZERO, ArrayList(), """
                    Soukromá farma s celkovou výměrou 42 ha, chov skotu. Pěstování a prodej vyloupané pšenice špaldy, žita a bílé hořčice.
                    Prodej přímo z farmy, po dohodě v Hradci Králové za cenu z farmy. Dovoz při vytížení kg o 3,-Kč dražší (ceny v závorce).
                    Pan Netík byl vyhlášen nejlepším ekologickým zemědělcem roku 1995.
                    Rodina Netíkových si peče doma kváskový chléb.""".trimIndent(),
                    "",
                    NetikSheetProcessor::class.qualifiedName!!, "jaroslav.netik@email.cz"
                ),
                Supplier("Daniel Buršík", BigDecimal.ZERO, ArrayList(), """Český pěstitel Quinoy""".trimIndent(),
                    "",
                    BursikSheetProcessor::class.qualifiedName!!, "Bursik.D@seznam.cz"
                ),

         )

        )

        return result
    }

    fun freeTransportFromVatValue(freeTransportIncludingVAT: Int) = withoutVAT(freeTransportIncludingVAT)
}
