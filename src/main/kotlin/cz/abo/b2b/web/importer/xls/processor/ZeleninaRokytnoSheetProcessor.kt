package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.OrderAttachment
import cz.abo.b2b.web.importer.googlesheet.GoogleSheetParser
import cz.abo.b2b.web.importer.xls.ExcelUtil.Companion.createHeaderRow
import cz.abo.b2b.web.importer.xls.ExcelUtil.Companion.createRows
import cz.abo.b2b.web.view.component.ViewUtils.Companion.round
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.util.*

/**
 * @author Tomas Kodym
 */
@Component
class ZeleninaRokytnoSheetProcessor() : AbstractSheetProcessor() {

    @Autowired
    lateinit var googleSheetParser: GoogleSheetParser

    override fun parseProducts(importSource: ImportSource, supplier: Supplier): List<Product> {
        val values = googleSheetParser.parseGoogleSheet(supplier.importUrl, "A1:D100")
        for (oneRow in values) {
          /*  val cell1  = oneRow.get
            val cell2  = oneRow[1]
            val cell3  = oneRow[2]
            val cell4  = oneRow[3]*/
            println(oneRow)
        }
        return emptyList()
    }

    override fun fillOrder(fileWithOrderAttachment: File, orderedProducts: Map<Product, Int>): OrderAttachment {
        val workbook = XSSFWorkbook();
        val sheet = workbook.createSheet()

        createHeaderRow(
            workbook, sheet, Arrays.asList(
                "Název",
                "MJ",
                "Objednávaný počet",
                "Cena/MJ",
                "Celkem bez DPH",
                "%",
            )
        )

        val rowsData: MutableList<List<Any>> = ArrayList()
        for (orderedItem in orderedProducts) {
            val product = orderedItem.key
            val orderedQuantity = orderedItem.value
            val oneRowData = listOf(
                product.productName,
                product.unit.name.lowercase(),
                orderedQuantity,
                round(product.priceNoVAT),
                round(product.priceNoVAT(orderedQuantity)),
                product.VAT * 100,
            )
            rowsData.add(oneRowData as List<Object>)
        }
        createRows(sheet, rowsData)

        return OrderAttachment("objednavka.xlsx", workbook)
    }

    override fun orderAttachmentFileName(supplier: Supplier): String {
        return "objednavka.xlsx"
    }


}
