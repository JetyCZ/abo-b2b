package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.xls.processor.AbstractSheetProcessor
import java.math.BigDecimal
import cz.abo.b2b.web.dao.UnitEnum
import cz.abo.b2b.web.importer.dto.OrderAttachment
import org.apache.commons.lang3.StringUtils
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.File
import java.util.*

/**
 * @author Tomas Kodym
 */
@Component
class ProbioSheetProcessor(val excelUtil: ExcelUtil) : AbstractSheetProcessor() {
    override val sheetName: String
        get() = "GASTRO+ BEZOBALU"

    override fun disintegrateIntoProduct(rowNum: Int, sheetData: List<String>?, supplier: Supplier): List<Product> {
        val itemsList: MutableList<Product> = ArrayList()
        //split values from list to array
        val values = sheetData!!.toTypedArray()
        if (values.size > 8) {
            if (StringUtils.isNumeric(values[8])) {
                val productName = values[2].trim { it <= ' ' }
                val itemQuantityStr = values[8].replaceFirst("\\s+kg".toRegex(), "")
                val itemQuantity = itemQuantityStr.toDouble()
                val itemPrice = values[10].toDouble()
                val itemTax = (values[9].toDouble() * 100).toInt()
                itemsList.add(
                    Product(
                        productName,
                        BigDecimal(itemPrice),
                        itemTax.toDouble(),
                        "",
                        BigDecimal(itemQuantity),
                        UnitEnum.KG,
                        null,
                        supplier
                    )
                )
            }
        }
        return itemsList
    }

    override fun fillOrder(fileToParse: File, orderedProducts: Map<Product, Int>): OrderAttachment {
        val workbook = XSSFWorkbook();
        val sheet = workbook.createSheet()

        excelUtil.createHeaderRow(
            workbook, sheet, Arrays.asList(
                "EAN",
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
            val orderedQuantity = orderedItem.value.toDouble()
            val oneRowData = listOf(
                product.ean,
                product.productName,
                product.unit.name.lowercase(),
                orderedQuantity,
                product.priceNoVAT.toDouble(),
                orderedQuantity * product.priceNoVAT.toDouble(),
                product.VAT * 100,
            )
            rowsData.add(oneRowData as List<Object>)
        }
        excelUtil.createRows(sheet, rowsData)

        return OrderAttachment("objednavka.xlsx", workbook)
    }

    override fun orderAttachmentFileName(supplier: Supplier): String {
        return "objednavka.xlsx"
    }
}