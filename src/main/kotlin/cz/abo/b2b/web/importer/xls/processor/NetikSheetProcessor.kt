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
class NetikSheetProcessor : AbstractSmallSupplierProcessor() {

    override fun parseProducts(importSource: ImportSource, supplier: Supplier): List<Product> {
        return Arrays.asList(
            Product("Bio žito", withoutVAT(17), 0.15, null, BigDecimal.ONE, UnitEnum.KG, null, supplier),
            Product("Bio Špalda", withoutVAT(39), 0.15, null, BigDecimal.ONE, UnitEnum.KG, null, supplier),
            Product("Bio hořčice bílá", withoutVAT(20), 0.15, null, BigDecimal.ONE, UnitEnum.KG, null, supplier)
        )
    }


}