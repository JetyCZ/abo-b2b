package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Shop
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.HeurekaXMLParser
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.ImportSource.Companion.fromFile
import cz.abo.b2b.web.importer.dto.OrderAttachment
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.poifs.filesystem.OfficeXmlFileException
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors

abstract class AbstractSheetProcessor {

    @Autowired
    lateinit var heurekaXMLParser: HeurekaXMLParser

    fun iterateSheetValues(formulaEvaluator: FormulaEvaluator, rowIterator: Iterator<Row>, supplier: Supplier): List<Product> {
        var row: Row
        val allProducts = ArrayList<Product>()

        //Iterate through all rows
        while (rowIterator.hasNext()) {
            row = rowIterator.next()
            val rowData: MutableList<String> = ArrayList()
            parseRow(row, formulaEvaluator, rowData)
            if (!rowData.isEmpty()) {
                val productList = disintegrateIntoProduct(row.rowNum, rowData, supplier)
                for (product in productList) {
                    if (product != null) {
                        allProducts.add(product)
                        product.rowNum = row.rowNum
                    }
                }
            }
        }
        return allProducts
    }

    abstract open fun disintegrateIntoProduct(rowNum: Int, rowData: List<String>?, supplier: Supplier): List<Product>

    fun parseProductsAsMap(importSource: ImportSource, supplier: Supplier): Map<String, Product?>? {
        val products = parseProducts(fromFile(importSource.path), supplier)
        val map: MutableMap<String, Product?> = TreeMap()
        var parsedIdx = 0
        for (product in products) {
            val key = product!!.productName + "_" + product.quantity.toInt()
            product.parseIdx = parsedIdx
            map[key] = product
            parsedIdx++
        }
        return map
    }

    open fun orderAttachmentFileName(supplier: Supplier): String? {
        return File(supplier.importUrl).name
    }

    open fun fillOrder(fileToParse: File, orderedProducts: Map<Product, Int>): OrderAttachment {
        val parsedExcel = getWorkbookFromFile(
            fromFile(fileToParse.path)
        )
        val workbook = parsedExcel.workbook
        val orderColumnIdx = orderColumnIdx()

        // Fill order not implemented yet
        if (orderColumnIdx == -1) return OrderAttachment(fileToParse.name, workbook)
        val orderSheet = getOrderSheetFromWorkbook(workbook)
        for ((product, orderQuantity) in orderedProducts) {
            setOrderQuantityForProduct(orderSheet, product, orderQuantity)
        }
        try {
            parsedExcel.excelFile.close()
        } catch (e: IOException) {
            throw IllegalStateException("Cannot close excel file", e)
        }
        return OrderAttachment(fileToParse.name, parsedExcel.workbook)
    }

    fun setOrderQuantityForProduct(orderSheet: Sheet?, product: Product, orderQuantity: Int) {
        val row = orderSheet!!.getRow(product.rowNum)
        row.createCell(orderColumnIdx()).setCellValue(orderQuantity.toDouble())
    }

    fun getOrderedQuantity(workbook: Workbook, rowNum: Int): Double {
        return getOrderSheetFromWorkbook(workbook)
            .getRow(rowNum)
            .getCell(orderColumnIdx())
            .numericCellValue
    }

    open fun orderColumnIdx(): Int {
        return -1
    }

    open fun freeTransportFrom(supplier: Supplier, shop: Shop): BigDecimal? {
        return supplier.freeTransportFrom
    }

    open fun parseProductsWithSupplier(supplier: Supplier, importSource: ImportSource): List<Product> {

        if (importSource.path.startsWith("http")) {
            return heurekaXMLParser.parseStream(importSource, supplier)
        }

        val products = parseProducts(importSource, supplier)
        return products
    }

    open fun parseProducts(importSource: ImportSource, supplier: Supplier): List<Product> {
        val sheet = getProductsSheetFromWorkbook(importSource)
        val iterator: Iterator<Row> = sheet!!.iterator()
        val formulaEvaluator = sheet.workbook.creationHelper.createFormulaEvaluator()
        LOGGER.info("Started parsing the values from the file with:" + this.javaClass.name)
        val products =
            iterateSheetValues(formulaEvaluator, iterator, supplier)
                .stream().filter { i: Product -> validateImportedObject(i) }.collect(Collectors.toList())
        return products
    }

    fun getProductsSheetFromWorkbook(inputSource: ImportSource): Sheet? {
        LOGGER.info("XXX before reading " + inputSource.path)
        val workbook = getWorkbookFromFile(inputSource).workbook
        LOGGER.info("XXX after reading " + inputSource.path)
        val productsSheetFromWorkbook = getProductsSheetFromWorkbook(workbook, sheetName)
        LOGGER.info("XXX after paresing " + inputSource.path)
        return productsSheetFromWorkbook
    }

    open fun getOrderSheetFromWorkbook(workbook: Workbook): Sheet {
        // Normally, product sheet is same as order sheet
        return getProductsSheetFromWorkbook(workbook, sheetName)
    }

    fun getProductsSheetFromWorkbook(workbook: Workbook, sheetName: String?): Sheet {
        var sheet: Sheet?
        sheet = if (sheetName == null) {
            workbook.getSheetAt(0)
        } else {
            workbook.getSheet(sheetName)
        }
        if (sheet == null) {
            sheet = workbook.getSheetAt(sheetIndexIfNameFails()!!)
        }
        return sheet
    }

    fun getWorkbookFromFile(importSource: ImportSource): ExcelFile {
        return try {
            var workbook: Workbook
            if (importSource.path.contains(XLS_EXTENSIONS)) try {
                workbook = HSSFWorkbook(importSource.newInputStream())
            } catch (e: OfficeXmlFileException) {
                var pkg: OPCPackage? = null
                pkg = OPCPackage.open(importSource.newInputStream())
                workbook = XSSFWorkbook(pkg)
            } else workbook = XSSFWorkbook(importSource.newInputStream())
            workbook.missingCellPolicy = Row.CREATE_NULL_AS_BLANK
            ExcelFile(workbook, importSource.newInputStream())
        } catch (e: Exception) {
            throw IllegalStateException("Cannot open workbook", e)
        }
    }

    open val sheetName: String?
        get() = null

    open fun sheetIndexIfNameFails(): Int? {
        return null
    }

    fun isRowEmpty(row: Row): Boolean {
        for (c in row.firstCellNum until row.lastCellNum) {
            val cell = row.getCell(c)
            if (cell != null && cell.cellType != Cell.CELL_TYPE_BLANK) return false
        }
        return true
    }

    fun validateImportedObject(product: Product): Boolean {
        return !product.productName.isEmpty() && product.productName != null &&
                product.quantity!=null && product.quantity.compareTo(BigDecimal(500))>0 &&
                product.priceNoVAT != null
    }

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
            Cell.CELL_TYPE_NUMERIC -> cell.numericCellValue.toString()
                .replaceFirst("\\.0+$".toRegex(), EMPTY_SPACE)
            Cell.CELL_TYPE_STRING -> cell.stringCellValue
            Cell.CELL_TYPE_BLANK -> EMPTY_SPACE
            Cell.CELL_TYPE_FORMULA -> formulaEvaluator.evaluate(cell).formatAsString()
                .replaceFirst("\\.0+$".toRegex(), EMPTY_SPACE)
            else -> cell.toString()
        }
        return value
    }

    class ExcelFile(var workbook: Workbook, var excelFile: InputStream) {
        fun setExcelFile(excelFile: FileInputStream) {
            this.excelFile = excelFile
        }
    }

    companion object {
        const val XLS_EXTENSIONS = ".xls"
        const val DELIMITER = ";"
        const val EMPTY_SPACE = ""
        @JvmStatic
        public val LOGGER = LoggerFactory.getLogger(AbstractSheetProcessor::class.java)
    }
}