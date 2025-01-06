package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.xls.processor.*
import cz.abo.b2b.web.view.component.MathUtils.Companion.withoutVAT
import org.springframework.stereotype.Component
import java.math.BigDecimal

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

                Supplier("Svět plodů", freeTransportFromVatValue(9999),
                    ArrayList(),
                    "Prémiové ořechy a sušené ovoce bez síření a přidaného cukru. Ochutnejte.",
                    "https://www.svetplodu.cz/feed/30/dc167ad430082a86a5ab492d25acb1b6b2f05231",
                    HeurekaXmlFeedProcessor::class.qualifiedName!!,
                    "VOobjednavka@svetplodu.cz"),

                Supplier("PROBIO(ONLY THEIR EXCEL)", BigDecimal(2500), ArrayList(),"", "https://www.probio.cz/data/product-feed/probio/8re6tf8erd5ordd23c7f59a63.xml",
                    HeurekaXmlFeedProcessor::class.qualifiedName!!, "objednavky@probio.cz"),

                Supplier("Wolfberry", BigDecimal(2000), ArrayList(), "", "http://cup.wolfberry.cz/xml-export/bezobalu_cz.xml",
                    WolfberrySheetProcessor::class.qualifiedName!!, "objednavky@wolfberry.cz"),

               Supplier("Sičaj",
                   freeTransportFromVatValue(2000), ArrayList(), "",
                   "http://www.sicajbezobalu.cz/google.xml",
                    SicajSheetProcessor::class.qualifiedName!!, "petr.sic@post.cz"),
                Supplier(
                    "bio nebio (USE e-shop)",
                    BigDecimal(3500),
                    ArrayList(),"",
                    "/bionebio/OL_bio_nebio_01_2025.xlsx",
                    BionebioSheetProcessor::class.qualifiedName!!,
                    "objednavky@bionebio.cz"
                ),
                Supplier("K-servis", BigDecimal(5000), ArrayList(),"", "/k-servis/2025_01_k-servis.xlsx", KServisSheetProcessor::class.qualifiedName!!, "k-servis@k-servis.com"),
                Supplier("Oříšek", BigDecimal(5000),ArrayList(), "",
                    "/orisek/orisek_1.12.2024.xls", NutSheetProcessor::class.qualifiedName!!, "orisek@orisek.cz"),

                Supplier("iPlody", BigDecimal(5000),ArrayList(), "",
                    "/mkm/iPlody_2025_01.xlsx", MkmPackSheetProcessor::class.qualifiedName!!, "martin@iplody.cz"),

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
                Supplier("Farma Němcova", BigDecimal(2500), ArrayList(),"", "https://www.farmanemcova.cz/exchange/5A68793B-3A12-4704-9313-56B0D45FEC97/xml/feed.xml",
                    HeurekaXmlFeedProcessor::class.qualifiedName!!, "objednavky@farmanemcova.cz")


         )

        )

        return result
    }

    fun freeTransportFromVatValue(freeTransportIncludingVAT: Int) = withoutVAT(freeTransportIncludingVAT)
}
