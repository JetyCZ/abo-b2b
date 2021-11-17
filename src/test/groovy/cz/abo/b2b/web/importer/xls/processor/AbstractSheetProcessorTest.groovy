package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import spock.lang.Specification

import static cz.abo.b2b.web.importer.dto.ImportSource.fromFile;

abstract class AbstractSheetProcessorTest extends Specification{
    def testSupplier = new Supplier("test",BigDecimal.ZERO, "","","" )
    protected Sheet fillWriteAndReadSheet(AbstractSheetProcessor processor, Integer additionalParsedIdx = null) {
        def sheetRead
        def filePath = getPricelistResourcePath()
        def f = resourceFilePath(filePath)

        def items = processor.parseItems(fromFile(f))
        Map<Product, Integer> orderedProducts = new HashMap<>();


        orderedProducts.put(items.get(0).toProduct(testSupplier), 3)
        orderedProducts.put(items.get(3).toProduct(testSupplier), 1)
        if (additionalParsedIdx!=null) {
            def item = items.get(additionalParsedIdx)
            def product = item.toProduct(testSupplier);
            orderedProducts.put(product, 1)
        }

        Workbook workbook = processor.fillOrder(new File(f), orderedProducts)
        def outputFilePath = System.getProperty("user.dir") + "/target"+ filePath
        org.apache.commons.io.FileUtils.forceMkdir(new File(outputFilePath).getParentFile())
        def outputStream = new FileOutputStream(outputFilePath)
        workbook.write(outputStream)
        outputStream.close()

        sheetRead = processor.getProductsSheetFromWorkbook(fromFile(outputFilePath))
        sheetRead
    }

    protected String getPricelistResourcePath() {
        return null;
    }

    protected String resourceFilePath(String relativeResourcesPath) {
        getClass().getResource(relativeResourcesPath).getFile().replace("%20", " ")
    }
}