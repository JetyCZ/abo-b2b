package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.importer.dto.ImportSource;
import cz.abo.b2b.web.importer.xls.dto.Item;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class MkmPackSheetProcessor extends AbstractSheetProcessor {
    String category = "";

    @Override
    public List<Item> parseItems(ImportSource importSource) {
        category = "";
        return super.parseItems(importSource);
    }

    @Override
    public List<Item> disintegrateIntoItem(int rowNum, List<String> sheetData) {
        List<Item> itemsList = new ArrayList<>();
        //split values from list to array
        String[] values = sheetData.toArray(new String[0]);

        if (values.length>=3) {
            // SUŠENÉ OVOCE BEZ CUKRU A SO2
            String name = values[1];
            if (!StringUtils.isEmpty(name) &&
                    StringUtils.isEmpty(values[2])
            ) {
                category = " (" + name + ")";
            }
        }
        if (values.length>6) {
            String priceStr5kg = readPrice(values[4]);

                String itemName = values[1].trim().replaceFirst("\\s+á?kg","");
                if (itemName.toUpperCase().contains("VYPRODÁNO")) return new ArrayList<>();
                itemName += category;
                int itemTax = 15;
                try{
                    double itemPrice = Double.valueOf(priceStr5kg)/5000;
                    double itemQuantity = 5000;
                    itemsList.add(new Item(itemName, itemQuantity, itemPrice, itemTax));
                } catch (NumberFormatException e) {
                    // This is OK
                    try {
                        // Packaging per 1kg
                        double itemPrice = Double.valueOf(readPrice(values[2]))/1000;
                        double itemQuantity = 1000;
                        itemsList.add(new Item(itemName, itemQuantity, itemPrice, itemTax));
                    } catch (NumberFormatException ex) {
                        // This is OK
                    }

                }


        }
        return itemsList;
    }

    public String readPrice(String value) {
        return value.replaceFirst("\\s*Kč", "");
    }

    @Override
    public int orderColumnIdx() {
        return 9;
    }

}
