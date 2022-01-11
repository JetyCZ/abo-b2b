package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.importer.HeurekaXMLParser;
import cz.abo.b2b.web.importer.xls.dto.Item;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tomas Kodym
 */
@Component
public class CountrySheetProcessor extends AbstractSheetProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountrySheetProcessor.class);

    private static final int PARSING_STARTER_ROW = 13;
    private static final int KILO_VALUE = 1000;
    private static final String KILOS_UPPER = "Kg";
    private static final String KILOS_LOWER = "kg";
    private static final List<String> QUANTITY_FORBIDDEN_VALUES = Arrays.asList("kg", "g");

    private static boolean isRowOmitted(int rowNumber)
    {
        return PARSING_STARTER_ROW > rowNumber;
    }

    @Override
    public List<Item> disintegrateIntoItem(int rowNum, List<String> rowData) {
        List<Item> items = new ArrayList<>();
        //Iterate through all rows
            if (isRowOmitted(rowNum)) return items;

            if (!rowData.get(1).isEmpty())
            {
                Item item = disintegrateIntoItemCountyLife(rowData);
                items.add(item);
            }
            return items;
    }

    private Item disintegrateIntoItemCountyLife(List<String> rowData)
    {
        try {
            String[] values = rowData.toArray(new String[0]);
            String itemName = values[9].trim();
            String priceStr = values[16];
            int itemTax = Integer.parseInt(values[18]);
            double itemQuantity;
            double itemPrice;

            if (checkIfQuantityValueIsPermitted(values[14])) {
                String ks_kg_baleni = values[15];
                itemQuantity = Double.valueOf(ks_kg_baleni) * 1000;
                itemPrice = Double.parseDouble(priceStr) / 1000;
            } else {
                String gramaz = values[13];
                if (checkIfQuantityValueIsPermitted(gramaz))
                {
                    itemQuantity = checkValuesInQuantityColumn(gramaz);
                    itemPrice = countValueForOneGram(Double.parseDouble(priceStr), itemQuantity);
                } else {
                    return null;
                }
            }
            Item item = new Item(itemName, itemQuantity, itemPrice, itemTax);
            if ("BIO".equals(values[10])) item.setBio(true);
            return item;
        } catch (NumberFormatException e) {
            System.out.println("Error:" + e.getMessage());
            return null;
        }
    }

    private void parseRow(Row row, FormulaEvaluator formulaEvaluator, List<String> rowData, int maxRow)
    {
        Cell cell;
        for (int i = 1; i < maxRow; i++)
        {
            cell = row.getCell(i);
            //Parse towards the cell type
            switch (cell.getCellType())
            {
                case Cell.CELL_TYPE_NUMERIC:
                    rowData.add(String.valueOf(cell.getNumericCellValue()).replaceFirst("\\.0+$", EMPTY_SPACE));
                    break;
                case Cell.CELL_TYPE_STRING:
                    rowData.add(cell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    rowData.add(EMPTY_SPACE);
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    rowData.add(formulaEvaluator.evaluate(cell).formatAsString().replaceFirst("\\.0+$", EMPTY_SPACE));
                    break;
                default:
                    rowData.add(String.valueOf(cell));
            }
        }
    }

    private Double checkValuesInQuantityColumn(String quantity)
    {
        if (quantity.contains(KILOS_LOWER) || quantity.contains(KILOS_UPPER))
        {
            double value = Double.parseDouble(StringUtils.getDigits(quantity));
            return value * KILO_VALUE;
        }
        else
            return Double.parseDouble(StringUtils.getDigits(quantity));
    }

    private boolean checkIfQuantityValueIsPermitted(String quantityValue)
    {
        if (quantityValue.isEmpty())
            return false;

        for (String s : QUANTITY_FORBIDDEN_VALUES)
        {
            if (quantityValue.contains(s))
                return true;
        }

        return false;
    }

    @Override
    public int orderColumnIdx() {
        return 22;
    }


}
