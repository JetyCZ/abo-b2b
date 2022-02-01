package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.xls.processor.AbstractSheetProcessor
import java.math.BigDecimal
import cz.abo.b2b.web.dao.UnitEnum
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.OrderAttachment
import cz.abo.b2b.web.importer.xls.ExcelUtil
import cz.abo.b2b.web.importer.xls.ExcelUtil.Companion.createHeaderRow
import cz.abo.b2b.web.importer.xls.ExcelUtil.Companion.createRows
import org.apache.commons.lang3.StringUtils
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Tomas Kodym
 */
@Component
class ProbioSheetProcessor() : AbstractSheetProcessor() {

    override fun parseProducts(importSource: ImportSource, supplier: Supplier): List<Product> {
        return heurekaXMLParser.parseStream(importSource, supplier)
    }

    override fun fillOrder(fileWithOrderAttachment: File, orderedProducts: Map<Product, Int>): OrderAttachment {
        val workbook = XSSFWorkbook();
        val sheet = workbook.createSheet()

        createHeaderRow(
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
        createRows(sheet, rowsData)

        return OrderAttachment("objednavka.xlsx", workbook)
    }

    override fun orderAttachmentFileName(supplier: Supplier): String {
        return "objednavka.xlsx"
    }


}