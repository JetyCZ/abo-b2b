package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.dao.Product;
import cz.abo.b2b.web.dao.Supplier;
import cz.abo.b2b.web.dao.UnitEnum;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tomas Kodym
 */

@Component
public class ProbioSheetProcessor extends AbstractSheetProcessor {

    @Override
    public String getSheetName() {
        return "GASTRO+ BEZOBALU";
    }

    @NotNull
    @Override
    public List<Product> disintegrateIntoProduct(int rowNum, List<String> sheetData, Supplier supplier) {
        List<Product> itemsList = new ArrayList<>();
        //split values from list to array
        String[] values = sheetData.toArray(new String[0]);
        if (values.length>8) {
            if (StringUtils.isNumeric(values[8])) {
                String productName = values[2].trim();
                String itemQuantityStr = values[8].replaceFirst("\\s+kg","");
                double itemQuantity = Double.parseDouble(itemQuantityStr)*1000;
                double itemPrice = Double.parseDouble(values[10])/1000;
                int itemTax = (int) (Double.parseDouble(values[9])*100);

                itemsList.add(new Product(productName, new BigDecimal(itemPrice), 0.15, "", new BigDecimal(itemQuantity), UnitEnum.KG, null, supplier));
            }
        }
        return itemsList;
    }

    @Override
    public Integer sheetIndexIfNameFails() {
        return 4;
    }

    @Override
    public int orderColumnIdx() {
        return 14;
    }
}
