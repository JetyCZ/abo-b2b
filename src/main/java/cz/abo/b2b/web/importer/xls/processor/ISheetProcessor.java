package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.dao.Product;
import cz.abo.b2b.web.importer.dto.ImportSource;
import cz.abo.b2b.web.importer.xls.dto.Item;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Tomas Kodym
 */

public interface ISheetProcessor
{
    String XLS_EXTENSIONS = ".xls";
    String DELIMITER = ";";
    String EMPTY_SPACE = "";
    Logger LOGGER = LoggerFactory.getLogger(ISheetProcessor.class);

    default List<Item> iterateSheetValues(FormulaEvaluator formulaEvaluator, Iterator<Row> rowIterator)
    {
        Row row;
        List<Item> allItems = new ArrayList();

        //Iterate through all rows
        while (rowIterator.hasNext())
        {
            row = rowIterator.next();

            List<String> rowData = new ArrayList<>();
            parseRow(row, formulaEvaluator, rowData);

            if (!rowData.isEmpty()) {
                List<Item> itemList = disintegrateIntoItem(row.getRowNum(), rowData);
                for (Item item : itemList) {
                    if (item!=null) {
                        allItems.add(item);
                        item.setRowNum(row.getRowNum());
                    }
                }
            }
        }
        return allItems;
    }

    List<Item> disintegrateIntoItem(int rowNum, List<String> rowData);

    default Map<String, Item> parseItemsAsMap(ImportSource importSource) {
        List<Item> items = parseItems(ImportSource.fromFile(importSource.getPath()));
        Map<String, Item> map = new TreeMap<String, Item>();
        int parsedIdx = 0;
        for (Item item : items) {
            String key = item.getItemName() + "_" + item.getItemQuantity().intValue() + (item.isBio() ? "_BIO" : "");
            item.parsedIdx = parsedIdx;
            map.put(key, item);
            parsedIdx++;
        }
        return map;
    }
    default Workbook fillOrder(File fileToParse, Map<Product, Integer> orderedItems){
        ExcelFile parsedExcel = getWorkbookFromFile(
                ImportSource.fromFile(fileToParse.getPath())
        );
        Workbook workbook = parsedExcel.getWorkbook();
        int orderColumnIdx = getOrderColumnIdx();

        // Fill order not implemented yet
        if (orderColumnIdx==-1) return workbook;
        Sheet orderSheet = getOrderSheetFromWorkbook(workbook);
        for (Map.Entry<Product, Integer> itemIntegerEntry : orderedItems.entrySet()) {
            Product product = itemIntegerEntry.getKey();
            Integer orderQuantity = itemIntegerEntry.getValue();
            setOrderQuantityForItem(orderSheet, product, orderQuantity);
        }
        try {
            parsedExcel.getExcelFile().close();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot close excel file", e);
        }
        return parsedExcel.getWorkbook();
    }

    default void setOrderQuantityForItem(Sheet orderSheet, Product product, Integer orderQuantity) {
        Row row = orderSheet.getRow(product.getRowNum());
        row.createCell(getOrderColumnIdx()).setCellValue(orderQuantity);
    }
    default double getOrderedQuantity(Workbook workbook, int rowNum) {
        return getOrderSheetFromWorkbook(workbook)
                .getRow(rowNum)
                .getCell(getOrderColumnIdx())
                .getNumericCellValue();
    }

    int getOrderColumnIdx();

    default List<Item> parseItems(ImportSource importSource) {
        Sheet sheet = getProductsSheetFromWorkbook(importSource);
        Iterator<Row> iterator = sheet.iterator();
        FormulaEvaluator formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        LOGGER.info("Started parsing the values from the file with:" + this.getClass().getName());

        List<Item> items = iterateSheetValues(formulaEvaluator, iterator);

        List<Item> validatedItems = items.stream().filter(i -> validateImportedObject(i)).collect(Collectors.toList());
        return validatedItems;
    }

    default Sheet getProductsSheetFromWorkbook(ImportSource inputSource) {
        LOGGER.info("XXX before reading " + inputSource.getPath());
        Workbook workbook = getWorkbookFromFile(inputSource).getWorkbook();
        LOGGER.info("XXX after reading " + inputSource.getPath());
        Sheet productsSheetFromWorkbook = getProductsSheetFromWorkbook(workbook, getSheetName());
        LOGGER.info("XXX after paresing " + inputSource.getPath());
        return productsSheetFromWorkbook;
    }

    default Sheet getOrderSheetFromWorkbook(Workbook workbook) {
        // Normally, product sheet is same as order sheet
        return getProductsSheetFromWorkbook(workbook, getSheetName());
    }

    default Sheet getProductsSheetFromWorkbook(Workbook workbook, String sheetName) {
        Sheet sheet;
        if (sheetName ==null) {
            sheet = workbook.getSheetAt(0);
        } else {
            sheet = workbook.getSheet(sheetName);
        }
        if (sheet==null) {
            sheet = workbook.getSheetAt(sheetIndexIfNameFails());
        }
        return sheet;
    }

    default ExcelFile getWorkbookFromFile(ImportSource importSource)  {
        try {
            Workbook workbook;

            if (importSource.getPath().contains(XLS_EXTENSIONS))
                try {
                    workbook = new HSSFWorkbook(importSource.newInputStream());
                } catch (OfficeXmlFileException e) {
                    OPCPackage pkg = null;
                        pkg = OPCPackage.open(importSource.newInputStream());
                        workbook = new XSSFWorkbook(pkg);
                }
            else
                workbook = new XSSFWorkbook(importSource.newInputStream());

            workbook.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);

            return new ExcelFile(workbook, importSource.newInputStream());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot open workbook", e);
        }
    }

    default String getSheetName() {
        return null;
    };

    default Integer sheetIndexIfNameFails() {
        return null;
    }

    default boolean isRowEmpty(Row row)
    {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++)
        {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                return false;
        }

        return true;
    }

    default boolean validateImportedObject(Item item)
    {
        return item != null && !item.getItemName().isEmpty() && item.getItemName() != null
                && !item.getItemQuantity().isNaN() && item.getItemQuantity() != null && item.getItemQuantity() >= 500
                && !item.getItemPrice().isNaN() && item.getItemPrice() != null && item.getItemName() != null;
    }

    default double countValueForOneGram(Double priceForKilos, Double itemQuantity)
    {
        return priceForKilos / itemQuantity;
    }

    default void cleanStringBuilder(StringBuilder stringBuilder)
    {
        stringBuilder.setLength(0);
    }

    default void parseRow(Row row, FormulaEvaluator formulaEvaluator, List<String> rowData)
    {
        Cell cell;
        int physicalNumberOfCells = row.getPhysicalNumberOfCells();
        for (int i = 0; i <= physicalNumberOfCells; i++)
        {
            cell = row.getCell(i);
            //Parse towards the cell type

            String value = getCellValue(formulaEvaluator, cell);
            rowData.add(value);

        }
    }

    default String getCellValue(FormulaEvaluator formulaEvaluator, Cell cell) {
        String value;
        switch (cell.getCellType())
        {
            case Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf(cell.getNumericCellValue()).replaceFirst("\\.0+$", EMPTY_SPACE);
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                value = EMPTY_SPACE;
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = formulaEvaluator.evaluate(cell).formatAsString().replaceFirst("\\.0+$", EMPTY_SPACE);
                break;
            default:
                value = String.valueOf(cell);
        }
        return value;
    }


    class ExcelFile {
        private Workbook workbook;
        private InputStream excelFile;

        public ExcelFile(Workbook workbook, InputStream excelFile) {
            this.workbook = workbook;
            this.excelFile = excelFile;
        }

        public Workbook getWorkbook() {
            return workbook;
        }

        public void setWorkbook(Workbook workbook) {
            this.workbook = workbook;
        }

        public InputStream getExcelFile() {
            return excelFile;
        }

        public void setExcelFile(FileInputStream excelFile) {
            this.excelFile = excelFile;
        }
    }
}