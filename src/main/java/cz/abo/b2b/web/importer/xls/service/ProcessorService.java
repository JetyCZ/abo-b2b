package cz.abo.b2b.web.importer.xls.service;

import cz.abo.b2b.web.dao.Product;
import cz.abo.b2b.web.dao.Supplier;
import cz.abo.b2b.web.dao.SupplierRepository;
import cz.abo.b2b.web.importer.xls.controller.dto.PriceListDTO;
import cz.abo.b2b.web.importer.xls.processor.ISheetProcessor;
import cz.abo.b2b.web.shoppingcart.ShoppingCart;
import cz.abo.b2b.web.shoppingcart.ShoppingCartItem;
import cz.abo.b2b.web.shoppingcart.ShoppingCartSupplier;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ProcessorService {

    private static final String UPLOADING_DIR = System.getProperty("user.dir") + "/uploadingDir/";

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ShoppingCart shoppingCart;

    @Autowired
    ApplicationContext applicationContext;


    public ISheetProcessor selectProcessor(Supplier supplier)
    {
        String importerClassName = supplier.getImporterClassName();
        try {
            return (ISheetProcessor) applicationContext.getBean(Class.forName(importerClassName));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot get importer", e);
        }
    }

    public PriceListDTO getFilledPriceListWithOrder(UUID supplierId) throws IOException {
        Supplier supplier = supplierRepository.getById(supplierId);

        String fileToParse = supplier.resourceFilePath();

        // Load file from database
        String contentType = "application/vnd.ms-excel";

        String pricelistFileName = "OL_bio nebio_11_2021.xls";
        String outputFilename = UPLOADING_DIR  + pricelistFileName;
        FileUtils.copyFile(new File(fileToParse), new File(outputFilename));

        Map<Product, Integer> orderedItems = new HashMap<>();
        ShoppingCartSupplier shoppingCartSupplier = shoppingCart.get(supplierId);
        for (ShoppingCartItem shoppingCartItem : shoppingCartSupplier.values()) {
            Integer count = new Integer((int) shoppingCartItem.getCount());
            orderedItems.put(shoppingCartItem.getProduct(), count);
        }

        ISheetProcessor sheetProcessor = selectProcessor(supplier);
        Workbook workbook = sheetProcessor.fillOrder(new File(outputFilename), orderedItems);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        FileUtils.deleteQuietly(new File(outputFilename));
        return new PriceListDTO(bos.toByteArray(), pricelistFileName, contentType);
    }
}
