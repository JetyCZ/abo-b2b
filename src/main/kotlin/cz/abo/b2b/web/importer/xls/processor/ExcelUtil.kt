package cz.abo.b2b.web.importer.xls.processor

import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component

@Component
class ExcelUtil {
    fun createRows(
        sheet: XSSFSheet,
        rowsData: MutableList<List<Any>>
    ) {
        // We start at second row, as first is header
        var rowNum = 1
        for (oneRowData in rowsData) {
            val productRow = sheet.createRow(rowNum++)
            var colIdx = 0
            for (cellContent in oneRowData) {
                val newCell = productRow.createCell(colIdx++)
                if (cellContent is Double) {
                    newCell.setCellValue(cellContent)
                } else if (cellContent is String) {
                    newCell.setCellValue(cellContent)
                } else {
                    newCell.setCellValue(cellContent.toString())
                }
            }
        }
    }

    fun createHeaderRow(
        workbook: XSSFWorkbook,
        sheet: XSSFSheet,
        labels: List<String>
    ) {
        var colIdx = 0
        val headerRow = sheet.createRow(0)
        val boldStyle = createBoldCellStyle(workbook)
        for (label in labels) {
            val headerCell = headerRow.createCell(colIdx++)
            headerCell.setCellValue(label)
            headerCell.cellStyle = boldStyle
        }
    }

    fun createBoldCellStyle(workbook: XSSFWorkbook): XSSFCellStyle? {
        val boldStyle = workbook.createCellStyle()
        boldStyle.borderTop = 6.toShort() // double lines border
        boldStyle.borderBottom = 1.toShort() // single line border
        val font = workbook.createFont()
        font.boldweight = XSSFFont.BOLDWEIGHT_BOLD
        boldStyle.setFont(font)
        return boldStyle
    }

}