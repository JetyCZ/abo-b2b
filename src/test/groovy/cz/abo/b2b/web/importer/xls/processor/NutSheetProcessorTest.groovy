package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.importer.dto.ImportSource

class NutSheetProcessorTest extends AbstractSheetProcessorTest {

    @Override
    protected String getPricelistResourcePath() {
        return  "/orisek/orisek_01.10.2021.xls"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new NutSheetProcessor().parseItemsAsMap(
                ImportSource.fromFile(f)
        )
        then:

        items.size() > 0
        def item1 = items["Aloe Vera_1000"]
        item1.itemTax == 15
        item1.itemPrice == 0.239

        def item2 = items["Zázvor v hořké čokoládě_3000"]
        item2.itemTax == 15
        item2.itemPrice == 0.141

        def item3 = items["Zeleninové chipsy_1400"]
        item3.itemTax == 15
        item3.itemPrice == 0.331


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