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
        if (values.length>6) {
            String priceStr5kg = readPrice(values[4]);

                String productName = values[1].trim().replaceFirst("\\s+á?kg","");
                if (productName.toUpperCase().contains("VYPRODÁNO")) return new ArrayList<>();
                productName += category;
                double productPrice = 0;
                double productQuantity = 0;
                try{
                    productPrice = Double.valueOf(priceStr5kg)/5;
                    productQuantity = 5;
                } catch (NumberFormatException e) {
                    // This is OK
                    try {
                        // Packaging per 1kg
                        productPrice = Double.valueOf(readPrice(values[6]));
                        productQuantity = 1;
                    } catch (NumberFormatException ex) {
                        if (!StringUtils.isEmpty(productName.trim())) {
                            String errorMsg = "Price cannot be determined: rowNum: %d; firstCell:%s".formatted(rowNum, values[0]);
                            getLOGGER().warn(errorMsg);
                        }
                    }
                }
                Product product = new Product(productName, new BigDecimal(productPrice), 0.15, "",
                    new BigDecimal(productQuantity), UnitEnum.KG, null, supplier);

                productList.add(product);


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
