package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.dao.Product;
import cz.abo.b2b.web.dao.Supplier;
import cz.abo.b2b.web.dao.UnitEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tomas Kodym
 */

@Component
public class NutSheetProcessor extends AbstractExcelSheetProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NutSheetProcessor.class);

    @Override
    public List<Product> disintegrateIntoProduct(int rowNum, @Nullable List<String> rowData, Supplier supplier) {
        if (!rowData.isEmpty())
        {
            String[] values = rowData.toArray(new String[0]);
            List<Product> productList = new ArrayList<>();
            if (values.length >= 6)
            {
                parseOneItem(values, productList, supplier);
            }
            return productList;
        }
        else {
            return new ArrayList<>();
        }
    }

    private void parseOneItem(String[] values, List<Product> productList, Supplier supplier) {
        String quantityStr = values[2].replaceAll(" ","");
        try {
            quantityStr = values[2].replaceAll(" ","");
            double productQuantityKg;
            if (quantityStr.contains("(")) {
                String productQuantityKgStr = StringUtils.substringBefore(quantityStr, "(");
                productQuantityKg = Double.parseDouble(productQuantityKgStr);
            } else {
                productQuantityKg = Double.parseDouble(quantityStr);
            }

            String productName = values[1];
            double productPrice = Double.parseDouble(values[3]);
            double vat = 0.15;

            String description = "Balení: " + quantityStr + "\n";

            Product product = new Product(productName, new BigDecimal(productPrice), vat, description,
                    new BigDecimal(productQuantityKg), UnitEnum.KG, null, supplier);
            productList.add(product);
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
