package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.dto.ImportSource.Companion.fromFile
import cz.abo.b2b.web.importer.dto.OrderAttachment
import cz.abo.b2b.web.importer.xls.dto.Item
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Tomas Kodym
 */
@Component
class WolfberrySheetProcessor : ISheetProcessor {
    override fun disintegrateIntoItem(rowNum: Int, rowData: MutableList<String>?): MutableList<Item> {
        TODO("Not yet implemented")
    }

    override fun getOrderColumnIdx(): Int {
        TODO("Not yet implemented")
    }

    override fun fillOrder(fileToParse: File?, orderedItems: Map<Product, Int>): OrderAttachment {
        val workbook = XSSFWorkbook();
        val sheet = workbook.createSheet()

        for (orderedItem in orderedItems.entries) {
            val row = sheet.createRow(0)
            val eanCell = row.createCell(0)
            eanCell.setCellValue(orderedItem.key.ean)
            val quantityCell = row.createCell(1)
            quantityCell.setCellValue(orderedItem.value.toDouble())
            val productNameCell = row.createCell(2)
            productNameCell.setCellValue(orderedItem.key.productName)
        }
        return OrderAttachment("objednavka.xlsx", workbook)
    }

    override fun orderAttachmentFileName(supplier: Supplier?): String {
        return "objednavka.xlsx"
    }
}