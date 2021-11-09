package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.importer.xls.dto.Item
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import spock.lang.Specification;

abstract class AbstractSheetProcessorTest extends Specification{
    protected Sheet fillWriteAndReadSheet(AbstractSheetProcessor processor, Integer additionalParsedIdx = null) {
        def sheetRead
        def filePath = getPricelistResourcePath()
        def f = resourceFilePath(filePath)

        def items = processor.parseItems(new File(f))


        Map<Item, Integer> orderedItems = new TreeMap<>();
        orderedItems.put(items.get(0), 3)
        orderedItems.put(items.get(3), 1)
        if (additionalParsedIdx!=null) {
            orderedItems.put(items.get(additionalParsedIdx), 1)
        }

        Workbook workbook = processor.fillOrder(new File(f), orderedItems)
        def outputFilePath = System.getProperty("user.dir") + "/target"+ filePath
        org.apache.commons.io.FileUtils.forceMkdir(new File(outputFilePath).getParentFile())
        def outputStream = new FileOutputStream(outputFilePath)
        workbook.write(outputStream)
        outputStream.close()

        sheetRead = processor.getProductsSheetFromWorkbook(new File(outputFilePath))
        sheetRead
    }

    protected String getPricelistResourcePath() {
        return null;
    }

    protected String resourceFilePath(String relativeResourcesPath) {
        getClass().getResource(relativeResourcesPath).getFile().replace("%20", " ")
    }
}
