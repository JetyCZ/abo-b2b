package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.dao.Product;
import cz.abo.b2b.web.dao.Supplier;
import cz.abo.b2b.web.dao.UnitEnum;
import cz.abo.b2b.web.importer.dto.ImportSource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
public class MkmPackSheetProcessor extends AbstractExcelSheetProcessor {
    String category = "";

    @NotNull
    @Override
    public List<Product> parseProducts(@NotNull ImportSource importSource, @NotNull Supplier supplier) {
        category = "";
        return super.parseProducts(importSource, supplier);
    }

    @Override
    public List<Product> disintegrateIntoProduct(int rowNum, @Nullable List<String> sheetData, Supplier supplier) {
        List<Product> productList = new ArrayList<>();
        //split values from list to array
        String[] values = sheetData.toArray(new String[0]);

        if (values.length>=3) {
            // SUŠENÉ OVOCE BEZ CUKRU A SO2
            String name = values[0];
            if (!StringUtils.isEmpty(name) &&
                    StringUtils.isEmpty(values[2])
            ) {
                category = " (" + name + ")";
            }
        }
        boolean validPriceFound = false;

        if (values.length>6) {
            String priceStrPerKg_5kgPackaging = readPrice(values[4]);

                String productName = values[1].trim().replaceFirst("\\s+á?kg","");
                if (productName.toUpperCase().contains("VYPRODÁNO")) return new ArrayList<>();
                productName += category;
                double productPrice = 0;
                double productQuantity = 0;
                try{
                    productPrice = Double.valueOf(priceStrPerKg_5kgPackaging);
                    productQuantity = 5;
                    validPriceFound = true;
                } catch (NumberFormatException e) {
                    // This is OK
                    try {
                        // Packaging per 1kg
                        String priceStrPerKg_OriginalPackaging = readPrice(values[6]);
                        productPrice = Double.valueOf(priceStrPerKg_OriginalPackaging);
                        productQuantity = 1;
                        validPriceFound = true;
                    } catch (NumberFormatException ex) {
                        if (!StringUtils.isEmpty(values[0].trim())) {
                            String errorMsg = "Price cannot be determined: rowNum: %d; firstCell:%s".formatted(rowNum, values[0]);
                            getLOGGER().warn(errorMsg);
                        }
                    }
                }
                if (validPriceFound) {
                    Product product = new Product(productName, new BigDecimal(productPrice), 0.15, "",
                            new BigDecimal(productQuantity), UnitEnum.KG, null, supplier);

                    productList.add(product);
                }


        }
        return productList;
    }

    public String readPrice(String value) {
        return value.replaceFirst("\\s*Kč", "");
    }

    @Override
    public int orderColumnIdx() {
        return 9;
    }

}
