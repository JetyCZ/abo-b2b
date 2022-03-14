package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Shop
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.dao.UnitEnum
import cz.abo.b2b.web.importer.impl.GoogleXMLParser
import cz.abo.b2b.web.importer.impl.HeurekaXMLParser
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.ImportSource.Companion.fromFile
import cz.abo.b2b.web.importer.dto.OrderAttachment
import cz.abo.b2b.web.importer.xls.ExcelUtil
import cz.abo.b2b.web.view.component.ViewUtils
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.math.BigDecimal
import java.util.*

abstract class AbstractSmallSupplierProcessor: AbstractSheetProcessor() {

    override fun fillOrder(fileWithOrderAttachment: File, orderedProducts: Map<Product, Int>): OrderAttachment {
        val workbook = XSSFWorkbook();
        val sheet = workbook.createSheet()

        ExcelUtil.createHeaderRow(
            workbook, sheet, Arrays.asList(
                "id",
                "Název",
                "MJ",
                "Velikost balení",
                "Objednávaný počet",
                "Cena/MJ s DPH",
                "Celkem s DPH",
                "%",
            )
        )

        val rowsData: MutableList<List<Any>> = ArrayList()
        for (orderedItem in orderedProducts) {
            val product = orderedItem.key
            val orderedQuantity = orderedItem.value
            val oneRowData = listOf(
                product.supplierCode,
                product.productName,
                product.unit.name.lowercase(),
                product.quantity,
                orderedQuantity,
                ViewUtils.round(product.priceVAT()),
                ViewUtils.round(product.priceVAT(orderedQuantity)),
                product.VAT * 100,
            )
            rowsData.add(oneRowData as List<Object>)
        }
        ExcelUtil.createRows(sheet, rowsData)

        return OrderAttachment("objednavka.xlsx", workbook)
    }

    override fun orderAttachmentFileName(supplier: Supplier): String {
        return "objednavka.xlsx"
    }
}