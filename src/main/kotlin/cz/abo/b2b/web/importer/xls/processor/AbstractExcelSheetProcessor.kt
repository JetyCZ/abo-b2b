package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.OrderAttachment
import cz.abo.b2b.web.importer.impl.HeurekaXMLParser
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.poifs.filesystem.OfficeXmlFileException
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.stream.Collectors

abstract class AbstractExcelSheetProcessor : AbstractSheetProcessor() {
    companion object {
        internal val LOGGER = LoggerFactory.getLogger(AbstractExcelSheetProcessor::class.java)
    }
    override fun parseProducts(importSource: ImportSource, supplier: Supplier): List<Product> {

        val sheet = getProductsSheetFromWorkbook(importSource)
        val iterator: Iterator<Row> = sheet!!.iterator()
        val formulaEvaluator = sheet.workbook.creationHelper.createFormulaEvaluator()
        LOGGER.info("Started parsing the values from the file with:" + this.javaClass.name)
        val products =
            iterateSheetValues(formulaEvaluator, iterator, supplier)
                .stream().filter { i: Product -> validateImportedObject(i) }.collect(Collectors.toList())
        return products
    }

    override fun fillOrder(fileWithOrderAttachment: File, orderedProducts: Map<Product, Int>): OrderAttachment {
        val parsedExcel = getWorkbookFromFile(
            ImportSource.fromFile(fileWithOrderAttachment.path)
        )
        val workbook = parsedExcel.workbook
        val orderColumnIdx = orderColumnIdx()

        // Fill order not implemented yet
        if (orderColumnIdx == -1) return OrderAttachment(fileWithOrderAttachment.name, workbook)
        val orderSheet = getOrderSheetFromWorkbook(workbook)
        for ((product, orderQuantity) in orderedProducts) {
            setOrderQuantityForProduct(orderSheet, product, orderQuantity)
        }
        HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook)

        try {
            parsedExcel.excelFile.close()
        } catch (e: IOException) {
            throw IllegalStateException("Cannot close excel file", e)
        }
        return OrderAttachment(fileWithOrderAttachment.name, parsedExcel.workbook)
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

    abstract fun disintegrateIntoProduct(rowNum: Int, rowData: List<String>?, supplier: Supplier): List<Product>
    abstract fun orderColumnIdx(): Int

    fun iterateSheetValues(formulaEvaluator: FormulaEvaluator, rowIterator: Iterator<Row>, supplier: Supplier): List<Product> {
        var row: Row
        val allProducts = ArrayList<Product>()

        //Iterate through all rows
        while (rowIterator.hasNext()) {
            row = rowIterator.next()
            val rowData: MutableList<String> = ArrayList()
            parseRow(row, formulaEvaluator, rowData)
            if (!rowData.isEmpty()) {

                try {
                    val productList = disintegrateIntoProduct(row.rowNum, rowData, supplier)
                    for (product in productList) {
                        if (product != null) {
                            allProducts.add(product)
                            product.rowNum = row.rowNum
                        }
                    }
                } catch (e: Exception) {
                    LOGGER.warn("Product from ${row.rowNum} import failed", e)
                }


            }
        }
        return allProducts
    }


    fun getWorkbookFromFile(importSource: ImportSource): AbstractSheetProcessor.ExcelFile {
        return try {
            var workbook: Workbook
            if (importSource.path.contains(AbstractSheetProcessor.XLS_EXTENSIONS)) try {
                workbook = HSSFWorkbook(importSource.newInputStream())
            } catch (e: OfficeXmlFileException) {
                var pkg: OPCPackage? = null
                pkg = OPCPackage.open(importSource.newInputStream())
                workbook = XSSFWorkbook(pkg)
            } else workbook = XSSFWorkbook(importSource.newInputStream())
            workbook.missingCellPolicy = Row.CREATE_NULL_AS_BLANK
            AbstractSheetProcessor.ExcelFile(workbook, importSource.newInputStream())
        } catch (e: Exception) {
            throw IllegalStateException("Cannot open workbook", e)
        }
    }

    fun getProductsSheetFromWorkbook(inputSource: ImportSource): Sheet? {
        AbstractSheetProcessor.LOGGER.info("XXX before reading " + inputSource.path)
        val workbook = getWorkbookFromFile(inputSource).workbook
        AbstractSheetProcessor.LOGGER.info("XXX after reading " + inputSource.path)
        val productsSheetFromWorkbook = getProductsSheetFromWorkbook(workbook, sheetName)
        AbstractSheetProcessor.LOGGER.info("XXX after paresing " + inputSource.path)
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
}