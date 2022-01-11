package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Shop
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.OrderAttachment
import cz.abo.b2b.web.importer.xls.dto.Item
import org.apache.commons.lang3.StringUtils
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.ArrayList

@Component
class DianaSheetProcessor : AbstractSheetProcessor() {
    private var prahaPsc = ArrayList<String>()

    init {
        prahaPsc.addAll(
            Arrays.asList(
        "10000",
        "10100",
        "10200",
        "10300",
        "10400",
        "10600",
        "10700",
        "10800",
        "10900",
        "11000",
        "11800",
        "11900",
        "12000",
        "12800",
        "13000",
        "14000",
        "14100",
        "14200",
        "14300",
        "14700",
        "14800",
        "14900",
        "15000",
        "15200",
        "15300",
        "15400",
        "15500",
        "15521",
        "15531",
        "15600",
        "15800",
        "15900",
        "16000",
        "16100",
        "16200",
        "16300",
        "16400",
        "16500",
        "16900",
        "17000",
        "17100",
        "18000",
        "18100",
        "18200",
        "18400",
        "18600",
        "19000",
        "19011",
        "19012",
        "19014",
        "19015",
        "19016",
        "19017",
        "19300",
        "19600",
        "19700",
        "19800",
        "19900",
        "25226",
        "25228"))
    }

/*
<zasoby>
	<zasoba>
		<kod>3-PH-20/100</kod>
		<nazev>Physalis natural 100g</nazev>
		<stat_puvodu>CN</stat_puvodu>
		<ean>8596443005396</ean>
		<nazev_1>Physalis natural 100g</nazev_1>
		<nazev_2>Physalis (Blasenkirschen) 100g</nazev_2>
		<mj>ks</mj>
		<hmotnost>0.103</hmotnost>
		<kratky_popis></kratky_popis>
		<popis_produktu></popis_produktu>
		<slozeni_produktu_cj>100% sušený physalis</slozeni_produktu_cj>
		<slozeni_produktu_aj>100% dried physalis</slozeni_produktu_aj>
		<energeticka_hodnota>1426/338</energeticka_hodnota>
		<bilkoviny>9.900000</bilkoviny>
		<tuky>3.700000</tuky>
		<nasycene_mastne_kyseliny>0.500000</nasycene_mastne_kyseliny>
		<mononasyc_mastne_kyseliny>1.600000</mononasyc_mastne_kyseliny>
		<polynasyc_mastne_kyseliny>1.600000</polynasyc_mastne_kyseliny>
		<trans_mastne_kyseliny></trans_mastne_kyseliny>
		<sacharidy>58.600000</sacharidy>
		<z_toho_cukry>31.400000</z_toho_cukry>
		<vlaknina>15.700000</vlaknina>
		<sul></sul>
		<vapnik></vapnik>
		<zelezo></zelezo>
		<fosfor></fosfor>
		<selen></selen>
		<horcik></horcik>
		<draslik></draslik>
		<cholesterol></cholesterol>
		<bez_pridaneho_cukru>true</bez_pridaneho_cukru>
		<bez_palmoveho_oleje>true</bez_palmoveho_oleje>
		<bez_lepku>true</bez_lepku>
		<bez_laktozy>true</bez_laktozy>
		<bez_ecek>true</bez_ecek>
		<bio_organicke>false</bio_organicke>
		<vegetarian>false</vegetarian>
		<vegan>false</vegan>
		<vysoky_obsah_bilkovin>false</vysoky_obsah_bilkovin>
		<dia>false</dia>
		<raw>false</raw>
		<kosher>false</kosher>
		<car_kod_karton>859644311595</car_kod_karton>
		<navod_na_pouziti></navod_na_pouziti>
		<alergeny></alergeny>
		<cross-kontaminace>5,6,</cross-kontaminace>
		<upozorneni_pro_spotrebitele></upozorneni_pro_spotrebitele>
		<skladovani>skladujte v suchu při teplotě nejvýše 22 oC a relativní vlhkosti nejvýše 70 %.</skladovani>
		<cena>31.9</cena>
	</zasoba>
 */

    override fun parseItemsWithSupplier(supplier: Supplier, importSource: ImportSource): List<Product> {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()

            val document = builder.parse(importSource.newInputStream())
            document.documentElement.normalize()
            val root = document.documentElement
            val result = ArrayList<Product>()
            val zasobaElems = root.getElementsByTagName("zasoba")
            for (i in 0 until zasobaElems.length) {
                try {
                    val zasoba = zasobaElems.item(i)
                    val zasobaChildren = zasoba.childNodes
                    val zasobaChildrenCount = zasobaChildren.length
                    var productName: String? = null
                    var description: String? = ""
                    var priceVAT: BigDecimal? = BigDecimal(0.15)
                    var bestBefore: LocalDate? = null
                    var vat = 0.15
                    var quantity: BigDecimal = BigDecimal.ONE
                    var ean: String? = null
                    for (j in 1 until zasobaChildrenCount) {
                        val shopItemChild = zasobaChildren.item(j)
                        val nodeName = shopItemChild.nodeName
                        if (shopItemChild.firstChild==null || StringUtils.isEmpty(shopItemChild.firstChild.nodeValue)) {
                            continue;
                        }
                        if ("nazev" == nodeName) {
                            productName = shopItemChild.firstChild.nodeValue
                        } else if ("slozeni_produktu_cj" == nodeName) {
                            description += "Složení: " + shopItemChild.firstChild.nodeValue + "\n"
                        } else if ("stat_puvodu" == nodeName) {
                            description += "Země původu: " + shopItemChild.firstChild.nodeValue + "\n"
                        } else if ("cena" == nodeName) {
                            var priceVatStr = shopItemChild.firstChild.nodeValue
                            priceVatStr = priceVatStr.replace(',', '.')
                            priceVAT = BigDecimal(priceVatStr.toDouble())
                        } else if ("hmotnost" == nodeName) {
                            var hmotnostStr = shopItemChild.firstChild.nodeValue
                            hmotnostStr = hmotnostStr.replace(',', '.')
                            quantity = BigDecimal(hmotnostStr.toDouble())
                        } else if ("ean" == nodeName) {
                            if (shopItemChild.firstChild!=null) {
                                ean = shopItemChild.firstChild.nodeValue
                            }
                        }
                    }
                    val product = Product(productName!!, priceVAT!!, vat, description, quantity, ean, supplier)
                    result.add(product)
                } catch (e: Exception) {
                    //TODO email problem with importing product
                    println(e)
                }
            }
            return result
    }

    override fun disintegrateIntoItem(rowNum: Int, rowData: List<String>?): List<Item> {
        TODO("Not yet implemented")
    }

    override fun orderColumnIdx(): Int {
        TODO("Not yet implemented")
    }

    override fun fillOrder(fileToParse: File, orderedItems: Map<Product, Int>): OrderAttachment {
        val workbook = XSSFWorkbook();
        val sheet = workbook.createSheet()
        for ((rowNum, orderedItem) in orderedItems.entries.withIndex()) {
            val row = sheet.createRow(rowNum)
            val eanCell = row.createCell(0)
            eanCell.setCellValue(orderedItem.key.ean)
            val quantityCell = row.createCell(1)
            quantityCell.setCellValue(orderedItem.value.toDouble())
            val productNameCell = row.createCell(2)
            productNameCell.setCellValue(orderedItem.key.productName)
        }
        return OrderAttachment("objednavka.xlsx", workbook)
    }

    override fun orderAttachmentFileName(supplier: Supplier): String {
        return "objednavka.xlsx"
    }

    override fun freeTransportFrom(supplier: Supplier, shop: Shop): BigDecimal? {
        if (prahaPsc.contains(shop.postcode)) {
            return BigDecimal(0.85 * 3000)
        }
        return super.freeTransportFrom(supplier, shop)
    }

}