package cz.abo.b2b.web.importer.xls.service;

import cz.abo.b2b.web.dao.Product;
import cz.abo.b2b.web.dao.Supplier;
import cz.abo.b2b.web.dao.SupplierRepository;
import cz.abo.b2b.web.importer.dto.ImportSource;
import cz.abo.b2b.web.importer.dto.ImportSourceType;
import cz.abo.b2b.web.importer.xls.controller.dto.FileAttachment;
import cz.abo.b2b.web.importer.xls.processor.AbstractSheetProcessor;
import cz.abo.b2b.web.state.shoppingcart.ShoppingCart;
import cz.abo.b2b.web.state.shoppingcart.ShoppingCartItem;
import cz.abo.b2b.web.state.shoppingcart.ShoppingCartSupplier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProcessorService {

    private static final String UPLOADING_DIR = System.getProperty("user.dir") + "/uploadingDir/";

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ShoppingCart shoppingCart;

    @Autowired
    ApplicationContext applicationContext;


    public AbstractSheetProcessor selectProcessor(Supplier supplier)
    {
        String importerClassName = supplier.getImporterClassName();
        try {
            return (AbstractSheetProcessor) applicationContext.getBean(Class.forName(importerClassName));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot get importer", e);
        }
    }

    public FileAttachment getFilledPriceListWithOrder(Long supplierId) throws IOException {
        Supplier supplier = supplierRepository.getById(supplierId);

        AbstractSheetProcessor abstractSheetProcessor = selectProcessor(supplier);

        ImportSource importSource = supplier.importSource();

        // Load file from database
        String contentType = "application/vnd.ms-excel";

        String pricelistFileName = abstractSheetProcessor.orderAttachmentFileName(supplier);

        String outputFilename = UPLOADING_DIR  + RandomStringUtils.randomAlphabetic(8) + "-" + new File(pricelistFileName).getName();

        // We will create copy of Excel file to be filled in
        if (importSource.getType().equals(ImportSourceType.FILE) ||
                importSource.getType().equals(ImportSourceType.CLASSPATH_RESOURCE)
        ) {
            IOUtils.copy(importSource.newInputStream(), new FileOutputStream(outputFilename));
        }

        Map<Product, Integer> orderedProducts = new HashMap<>();
        ShoppingCartSupplier shoppingCartSupplier = shoppingCart.get(supplierId);
        for (ShoppingCartItem shoppingCartItem : shoppingCartSupplier.values()) {
            Integer count = (int) shoppingCartItem.getCount();
            if (count>0)
                orderedProducts.put(shoppingCartItem.getProduct(), count);
        }

        Workbook workbook = abstractSheetProcessor.fillOrder(new File(outputFilename), orderedProducts).getWorkbook();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        FileUtils.deleteQuietly(new File(outputFilename));
        return new FileAttachment(bos.toByteArray(), pricelistFileName, contentType);
    }
}
