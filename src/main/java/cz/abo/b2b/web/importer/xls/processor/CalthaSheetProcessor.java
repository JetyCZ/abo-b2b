package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.dao.*;
import cz.abo.b2b.web.importer.dto.OrderAttachment;
import cz.abo.b2b.web.security.SecurityService;
import cz.abo.b2b.web.view.component.MathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tomas Kodym
 */

@Component
public class CalthaSheetProcessor extends AbstractExcelSheetProcessor {

    String lastCategory = null;
    String lastItemName = null;

    @Autowired
    SecurityService securityService;

    @Override
    public List<Product> disintegrateIntoProduct(int rowNum, List<String> sheetData, Supplier supplier) {
        List<Product> productList = new ArrayList<>();
        //split values from list to array
        String[] values = sheetData.toArray(new String[0]);
        if (values.length>7) {
            String priceStr = values[4].replaceFirst("\\s*Kč","").trim();
            String ean = values[3];
            String valueInNameColumn = values[1].trim();
            if (!StringUtils.isEmpty(values[0])) {
                lastCategory = values[0].replace("\n"," ");
            }
            if (StringUtils.isNumeric(priceStr)) {
                if (!StringUtils.isEmpty(valueInNameColumn)) {
                    lastItemName = valueInNameColumn;
                }
                String itemName = lastItemName;

                if (!StringUtils.isEmpty(lastCategory)) {
                    itemName += " (" + lastCategory + ")";
                }
                Double itemQuantity = -1D;

                String itemQuantityStr = values[2];
                if (itemQuantityStr!=null) itemQuantityStr = itemQuantityStr.replace("\\,", "\\.");
                UnitEnum unitEnum;
                if (itemQuantityStr.equals("kus") || itemQuantityStr.equals("1 ks")) {
                    itemQuantity = 1D;
                    unitEnum = UnitEnum.KS;
                } else if (itemQuantityStr.endsWith(" ks")) {
                    String quantityStr = StringUtils.substringBefore(itemQuantityStr, " ks");
                    itemQuantity = MathUtils.toDouble(quantityStr);
                    unitEnum = UnitEnum.KS;
                } else if (itemQuantityStr.endsWith(" ml")) {
                    String quantityStr = StringUtils.substringBefore(itemQuantityStr, " ml");
                    itemQuantity = MathUtils.toDouble(quantityStr)/1000;
                    unitEnum = UnitEnum.L;
                } else if (itemQuantityStr.endsWith(" kg")) {
                    String quantityStr = StringUtils.substringBefore(itemQuantityStr, " kg");
                    itemQuantity = MathUtils.toDouble(quantityStr);
                    unitEnum = UnitEnum.KG;
                } else if (itemQuantityStr.endsWith(" g/kus")) {
                    itemQuantityStr = StringUtils.substringBefore(itemQuantityStr, " g/kus");
                    itemQuantity = 1D;
                    unitEnum = UnitEnum.KS;
                } else if (itemQuantityStr.endsWith(" g")) {
                    String quantityStr = StringUtils.substringBefore(itemQuantityStr, " g");
                    itemQuantity = MathUtils.toDouble(quantityStr)/1000;
                    unitEnum = UnitEnum.KG;
                } else if (StringUtils.isEmpty(itemQuantityStr)) {
                    // This is header or footer row
                    return productList;
                } else {
                    throw new IllegalStateException("Unkonwn itemQuantityStr " + itemQuantityStr);
                }

                double itemPrice = Double.parseDouble(priceStr)/itemQuantity;

                String description = "<b>Kategorie:</b> " + lastCategory + "<br>";
                description += "<b>Hmotnost:</b> " + itemQuantityStr;
                description += "<b>EAN:</b> " + ean;
                Product product = new Product(itemName, new BigDecimal(itemPrice), 0.15, description, new BigDecimal(itemQuantity), unitEnum, ean, supplier);
                productList.add(product);


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
        Sheet productsSheet = getProductsSheetFromWorkbook(orderAttachment.getWorkbook(), getSheetName());
        User user = securityService.authenticatedDbUser();
        Shop shop = user.getShop();
        productsSheet.getRow(2).getCell(1).setCellValue(
                shop.getName() + "\n"
                + shop.getStreet() + "\n"
                + shop.getPostcode() + " " + shop.getCity() + "\n"
                + "IČO:" + shop.getDic()
        );
        productsSheet.getRow(3).getCell(1).setCellValue(
                new SimpleDateFormat("dd.MM.yyyy").format(new Date())
        );
        return orderAttachment;
    }

    @Nullable
    @Override
    public String getSheetName() {
        return "Bezobalový prodej";
    }

    @Override
    public double validMinimalProductWeight() {
        return 0;
    }
}
