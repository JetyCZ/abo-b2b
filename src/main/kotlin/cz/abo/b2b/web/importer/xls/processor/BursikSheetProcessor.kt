package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.dao.UnitEnum
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.OrderAttachment
import cz.abo.b2b.web.importer.xls.ExcelUtil
import cz.abo.b2b.web.view.component.MathUtils
import cz.abo.b2b.web.view.component.MathUtils.Companion.withoutVAT
import cz.abo.b2b.web.view.component.ViewUtils.Companion.round
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.File
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@Component
class BursikSheetProcessor : AbstractSmallSupplierProcessor() {
    override fun parseProducts(importSource: ImportSource, supplier: Supplier): List<Product> {
        val description = """Quinoa, merlík čilský, v jazyce Inků „matka zrn" je bezlepková pseudoobilnina původem z Jižní Ameriky, kde byla před pěti tisíci let domestikována. V posledním desetiletí se díky rostoucí poptávce po zdravých potravinách dostala do hledáčků výzkumných institucí v Evropě, které zkoumají možnosti pěstování v našich podmínkách. V České republice jsou průkopníky s Quinoou vědci z Mendelovy univerzity v Brně, kteří prostřednictvím polních pokusů zkoumají jednotlivé odrůdy a jejich potenciál pro jejich pěstování v ČR.
Většina v jižní Americe běžně pěstovaných odrůd obsahuje na povrchu zrnek vrstvu hořkých saponinů, které slouží jako přirozená ochrana proti škůdcům, a které se hned po sklizni buď obrušují nebo vymývají.
Pro první pěstování v České republice byla vybrána odrůda s nízkým obsahem hořkých saponinů, která nevyžaduje průmyslové obrušování a čištění, pro které nemáme v ČR přizpůsobené technologie. Na Vysočině sme začali s pěstováním Quinoy v roce 2020 a po nezbytných přípravách, testech a kuchařských pokusech jsme se rozhodli uvést část produkce na trh koncovým spotřebitelům.
Vzhledem k tomu, že Naši Quinou dodáváme přírodní, je nutné ji před vařením zbavit hořké chuti. Odváženou quinou nechte na 15 minut odmočit v teplé vodě a potom properte a pláchněte v sítku pod tekoucí vlažnou vodou. Poté již připravujeme jako běžně dostupnou quinou. V poměru 1:2 s vodou ji uveďte do varu a povařte doměkka (cca 20 minut). """
        return Arrays.asList(
            Product("Quinoa", withoutVAT(100), 0.15, description, BigDecimal.ONE, UnitEnum.KG, null, supplier),
            Product("Quinoa", withoutVAT(80), 0.15, description, BigDecimal(40), UnitEnum.KG, null, supplier),
            Product("Quinoa", withoutVAT(70), 0.15, description, BigDecimal(160), UnitEnum.KG, null, supplier),
            Product("Quinoa", withoutVAT(65), 0.15, description, BigDecimal(400), UnitEnum.KG, null, supplier),
        )
    }


}