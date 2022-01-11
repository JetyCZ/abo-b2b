package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.HeurekaXMLParser
import cz.abo.b2b.web.importer.dto.OrderAttachment
import cz.abo.b2b.web.importer.xls.dto.Item
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.File

@Component
class WolfberrySheetProcessor : AbstractSheetProcessor() {

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
}