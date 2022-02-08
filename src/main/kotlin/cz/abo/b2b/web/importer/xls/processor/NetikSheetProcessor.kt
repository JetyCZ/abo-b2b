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
class NetikSheetProcessor : AbstractSheetProcessor() {

    override fun parseProducts(importSource: ImportSource, supplier: Supplier): List<Product> {
        return Arrays.asList(
            Product("Bio žito", withoutVAT(17), 0.15, null, BigDecimal.ONE, UnitEnum.KG, null, supplier),
            Product("Bio Špalda", withoutVAT(39), 0.15, null, BigDecimal.ONE, UnitEnum.KG, null, supplier),
            Product("Bio hořčice bílá", withoutVAT(20), 0.15, null, BigDecimal.ONE, UnitEnum.KG, null, supplier)
        )
    }

    override fun fillOrder(fileWithOrderAttachment: File, orderedProducts: Map<Product, Int>): OrderAttachment {
        val workbook = XSSFWorkbook();
        val sheet = workbook.createSheet()

        ExcelUtil.createHeaderRow(
            workbook, sheet, Arrays.asList(
                "id",
                "Název",
                "MJ",
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
                orderedQuantity,
                round(product.priceVAT()),
                round(product.priceVAT(orderedQuantity)),
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