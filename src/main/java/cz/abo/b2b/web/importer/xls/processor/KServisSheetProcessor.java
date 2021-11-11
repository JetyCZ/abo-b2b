package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.dao.Product;
import cz.abo.b2b.web.importer.xls.dto.Item;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tomas Kodym
 */

@Component
public class KServisSheetProcessor extends AbstractSheetProcessor
{
    String lastCategory = null;

    @Override
    public List<Item> disintegrateIntoItem(int rowNum, List<String> sheetData) {
        List<Item> itemsList = new ArrayList<>();
        //split values from list to array
        String[] values = sheetData.toArray(new String[0]);
        if (values.length>5) {
            String priceStr = values[5].replaceFirst("\\s*Kč","").trim();
            String valueInNameColumn = values[1].trim();
            if (priceStr.contains("Cena za kg")) {
                lastCategory = valueInNameColumn;
            }

            if (StringUtils.isNumeric(priceStr)) {
                String itemName = valueInNameColumn;
                if (!StringUtils.isEmpty(lastCategory)) {
                    itemName += " (" + lastCategory + ")";
                }
                String itemQuantityStr = values[4];
                Pattern weightPattern = Pattern.compile("^(?<weight>.+?)(\\(.*\\)|\\/.*)?$");
                Matcher matcher = weightPattern.matcher(itemQuantityStr);
                if (matcher.matches()) {
                    String itemQuantityParsed = matcher.group("weight")
                            .replaceFirst("\\,", "\\.")
                            .replaceFirst("x1kg", "")
                            .replaceFirst("kg","");

                    double itemQuantity = Double.parseDouble(itemQuantityParsed)*1000;
                    double itemPrice = Double.parseDouble(priceStr)/1000;
                    int itemTax = 15;
                    Item item = new Item(itemName, itemQuantity, itemPrice, itemTax);
                    itemsList.add(item);
                    item.description = "<br><b>Balení:</b> " + itemQuantityStr + "<br>";
                    item.description += "<b>Bez přid. cukru:</b> " + (("X".equals(values[2]))?"Ano":"Ne") + "<br>";
                    item.description += "<b>Bez SO2:</b> " + (("X".equals(values[3]))?"Ano":"Ne") + "<br>";

                }


            }
        }
        return itemsList;
    }

    public int getOrderColumnIdx() {
        return 6;
    }

    @Override
    public Workbook fillOrder(File fileToParse, Map<Product, Integer> orderedItems) {
        Workbook workbook = super.fillOrder(fileToParse, orderedItems);
        getProductsSheetFromWorkbook(workbook).getRow(0).getCell(6).setCellValue("Objednávám tolik balení");
        return workbook;
    }
}
