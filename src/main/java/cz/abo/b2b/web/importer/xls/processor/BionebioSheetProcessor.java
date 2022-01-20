package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.dao.Product;
import cz.abo.b2b.web.dao.Supplier;
import cz.abo.b2b.web.dao.UnitEnum;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tomas Kodym
 */

@Component
public class BionebioSheetProcessor extends AbstractSheetProcessor {
    public static final Double EUR_TO_CZK = new Double(26);
    private static final Logger log = LoggerFactory.getLogger(BionebioSheetProcessor.class);

    @Override
    public String getSheetName() {
        return "Velká balení";
    }

    Pattern nameWithWeight = Pattern.compile("^(?<itemName>.*?)(?<weight>[\\d\\,]+)\\skg$");

    Double parsedEurValue = null;

    @Override
    public List<Product> disintegrateIntoProduct(int rowNum, @Nullable List<String> rowData, Supplier supplier) {
        List<Product> itemsList = new ArrayList<>();

        //split values from list to array
        String[] values = rowData.toArray(new String[0]);


        if (values.length>=4) {
            if (rowNum ==2 && values.length>=4) {
                parsedEurValue = Double.parseDouble(values[3]);
            }
            String productName = values[1].trim();
            if (productName.endsWith(" kg")) {
                Matcher matcher = nameWithWeight.matcher(productName);
                if (matcher.matches()) {
                    String productNameToUse = matcher.group("itemName").trim();
                    String productQuantityStr = matcher.group("weight");
                    productQuantityStr = productQuantityStr.replaceFirst("\\,","\\.");
                    double quantityKg = Double.parseDouble(productQuantityStr);
                    Double productPrice = null;
                    if (values[2].length()>0) {
                        productPrice = Double.parseDouble(values[2]);
                    } else {
                        String eurColumnValue = values[3];
                        if (!StringUtils.isEmpty(eurColumnValue)) {
                            double eurValue = Double.parseDouble(eurColumnValue);
                            Double eurToCzk = parsedEurValue==null?EUR_TO_CZK:parsedEurValue;
                            productPrice = eurValue * eurToCzk;
                        } else {
                            log.warn("No price for: " + productNameToUse);
                        }
                    }

                    String note = values[7];
                    String description = "";
                    if (!StringUtils.isEmpty(note)) {
                        if (note.contains("Cena za celé balení")) {
                            productPrice = productPrice/quantityKg;
                        } else {
                            description = note;
                        }
                    }
                    if (values.length>8) {
                        description += "<br><b>Výrobce/dodavatel</b>: " + values[8];
                    }


                    if (productPrice!=null) {
                        Product product = new Product(productNameToUse, new BigDecimal(productPrice), 0.15, description, new BigDecimal(quantityKg), UnitEnum.KG, null, supplier);
                        itemsList.add(product);
                    }
                }

            }
        }
        return itemsList;
    }

    @Override
    public int orderColumnIdx() {
        return 4;
    }


}
