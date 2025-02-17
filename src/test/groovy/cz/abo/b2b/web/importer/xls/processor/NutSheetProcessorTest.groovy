package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.importer.dto.ImportSource

class NutSheetProcessorTest extends AbstractSheetProcessorTest {

    @Override
    protected String getPricelistResourcePath() {
        return  "/orisek/orisek_1.12.2024.xls"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new NutSheetProcessor().parseProductsAsMap(
                ImportSource.fromFile(f), testSupplier
        )
        then:

        items.size() > 0
        def item1 = items["Aloe Vera_1"]
        item1.VAT == 0.15
        item1.priceNoVAT == 239

        def item2 = items["Zázvor v hořké čokoládě_3"]
        item2.VAT == 0.15
        item2.priceNoVAT == 141

        def item3 = items["Zeleninové chipsy_1.4"]
        item3.VAT == 0.15
        item3.priceNoVAT == 331
        item3.quantity == 1.4


    }

    def "Make Order"() {
        given:
        def processor = new NutSheetProcessor()
        def sheetRead = fillWriteAndReadSheet(processor)
        def sheetWithOrder = sheetRead.getWorkbook().getSheet("Objednávka")

        expect:
        sheetWithOrder.getRow(3).getCell(5).getNumericCellValue() == 3
        sheetWithOrder.getRow(6).getCell(5).getNumericCellValue() == 1

    }
}
