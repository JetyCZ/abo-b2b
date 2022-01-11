package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.importer.xls.dto.Item;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tomas Kodym
 */

@Component
public class NutSheetProcessor extends AbstractSheetProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NutSheetProcessor.class);

    @Override
    public List<Item> disintegrateIntoItem(int rowNum, List<String> rowData) {
        if (!rowData.isEmpty())
        {
            String[] values = rowData.toArray(new String[0]);
            List<Item> itemsList = new ArrayList<>();
            if (values.length >= 6)
            {
                parseOneItem(values, itemsList);
            }
            return itemsList;
        }
        else {
            return new ArrayList<>();
        }
    }

    private void parseOneItem(String[] values, List<Item> itemsList) {
        String quantityStr = values[2];
        double itemQuantityDouble = 0;
        try {
            itemQuantityDouble = Double.parseDouble(quantityStr);
            String itemName = values[1];
            double itemQuantity = countKilosToGrams(itemQuantityDouble);
            double itemPrice = Double.parseDouble(values[3])/1000;
            int itemTax = (int) (Double.parseDouble(values[4])*100);
            itemsList.add(new Item(itemName, itemQuantity, itemPrice, itemTax));
        } catch (NumberFormatException e) {
            LOGGER.warn("Item was not created, because of non numeric value " + quantityStr +
                    "in quantity column.");
        }


    }

    private double countKilosToGrams(Double kilos)
    {
        return kilos * 1000;
    }

    @Override
    public Sheet getOrderSheetFromWorkbook(Workbook workbook) {
        return super.getOrderSheetFromWorkbook(workbook).getWorkbook().getSheet("Objednávka");
    }

    @Override
    public int orderColumnIdx() {
        return 5;
    }

    @Override
    public String getSheetName() {
        return "Objednávka";
    }



}
