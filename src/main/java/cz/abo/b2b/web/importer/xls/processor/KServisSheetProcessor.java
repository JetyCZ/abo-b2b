package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.dao.Product;
import cz.abo.b2b.web.dao.Supplier;
import cz.abo.b2b.web.dao.UnitEnum;
import cz.abo.b2b.web.importer.dto.OrderAttachment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tomas Kodym
 */

@Component
public class KServisSheetProcessor extends AbstractExcelSheetProcessor {
    String lastCategory = null;

    @Override
    public List<Product> disintegrateIntoProduct(int rowNum, List<String> sheetData, Supplier supplier) {
        List<Product> productList = new ArrayList<>();
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

                    double itemQuantity = Double.parseDouble(itemQuantityParsed);
                    double itemPrice = Double.parseDouble(priceStr);

                    String description = "<b>Hmotnost balení v kg:</b> " + itemQuantityStr + "<br>";
                    description = "<b>Bez přid. cukru:</b> " + (("X".equals(values[2]))?"Ano":"Ne") + "<br>";
                    description += "<b>Bez SO2:</b> " + (("X".equals(values[3]))?"Ano":"Ne") + "<br>";
                    Product product = new Product(itemName, new BigDecimal(itemPrice), 0.15, description, new BigDecimal(itemQuantity), UnitEnum.KG, null, supplier);
                    productList.add(product);
                }


            }
        }
        return productList;
    }
    @Override
    public int orderColumnIdx() {
        return 6;
    }

    @Override
    public OrderAttachment fillOrder(File fileWithOrderAttachment, Map<Product, Integer> orderedProducts) {
        OrderAttachment orderAttachment = super.fillOrder(fileWithOrderAttachment, orderedProducts);
        getProductsSheetFromWorkbook(orderAttachment.getWorkbook(), getSheetName()).getRow(0).getCell(orderColumnIdx()).setCellValue("Objednávám tolik balení");
        return orderAttachment;
    }


}
