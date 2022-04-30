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
import org.apache.poi.ss.usermodel.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.math.BigDecimal
import java.util.*

abstract class AbstractSheetProcessor {

    @Autowired
    lateinit var heurekaXMLParser: HeurekaXMLParser

    @Autowired
    lateinit var googleXMLParser: GoogleXMLParser

    fun parseProductsAsMap(importSource: ImportSource, supplier: Supplier): Map<String, Product?>? {
        val products = parseProducts(fromFile(importSource.path), supplier)
        val map: MutableMap<String, Product?> = TreeMap()
        var parsedIdx = 0
        for (product in products) {
            val key = product!!.productName + "_" + product.quantity.toPlainString()
            product.parseIdx = parsedIdx
            map[key] = product
            parsedIdx++
        }
        return map
    }

    open fun orderAttachmentFileName(supplier: Supplier): String? {
        return File(supplier.importUrl).name
    }

    open fun freeTransportFrom(supplier: Supplier, shop: Shop): BigDecimal? {
        return supplier.freeTransportFrom
    }

    abstract fun parseProducts(importSource: ImportSource, supplier: Supplier): List<Product>

    abstract fun fillOrder(fileWithOrderAttachment: File, orderedProducts: Map<Product, Int>): OrderAttachment

    fun validateImportedObject(product: Product): Boolean {
        if (product.unit == UnitEnum.KG && product.quantity.compareTo(BigDecimal(validMinimalProductWeight()))<0) {
            return false;
        }
        return !product.productName.isEmpty() && product.productName != null &&
                product.quantity!=null &&
                product.priceNoVAT != null
    }

    /**
     * During import, ignore products, that weight less then this value (in kg)
     */
    open fun validMinimalProductWeight() = 0.5

    fun countValueForOneGram(priceForKilos: Double, productQuantity: Double): Double {
        return priceForKilos / productQuantity
    }

    fun cleanStringBuilder(stringBuilder: StringBuilder) {
        stringBuilder.setLength(0)
    }

    fun parseRow(row: Row, formulaEvaluator: FormulaEvaluator, rowData: MutableList<String>) {
        var cell: Cell
        val physicalNumberOfCells = row.physicalNumberOfCells
        for (i in 0..physicalNumberOfCells) {
            cell = row.getCell(i)
            //Parse towards the cell type
            val value = getCellValue(formulaEvaluator, cell)
            rowData.add(value)
        }
    }

    fun getCellValue(formulaEvaluator: FormulaEvaluator, cell: Cell): String {
        val value: String
        value = when (cell.cellType) {
            Cell.CELL_TYPE_NUMERIC -> BigDecimal(cell.numericCellValue).toPlainString()
                .replaceFirst("\\.0+$".toRegex(), EMPTY_SPACE)
            Cell.CELL_TYPE_STRING -> cell.stringCellValue
            Cell.CELL_TYPE_BLANK -> EMPTY_SPACE
            Cell.CELL_TYPE_FORMULA -> formulaEvaluator.evaluate(cell).formatAsString()
                .replaceFirst("\\.0+$".toRegex(), EMPTY_SPACE)
            else -> cell.toString()
        }
        return value
    }

    class ExcelFile(var workbook: Workbook, var excelFile: InputStream)

    companion object {
        const val XLS_EXTENSIONS = ".xls"
        const val DELIMITER = ";"
        const val EMPTY_SPACE = ""
        @JvmStatic
        val LOGGER = LoggerFactory.getLogger(AbstractSheetProcessor::class.java)
    }
}