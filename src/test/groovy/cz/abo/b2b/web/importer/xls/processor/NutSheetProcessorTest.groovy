package cz.abo.b2b.web.importer.xls.processor





class NutSheetProcessorTest extends AbstractSheetProcessorTest {

    @Override
    protected String getPricelistResourcePath() {
        return  "/orisek/orisek_01.03.2019.xls"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new NutSheetProcessor().parseItemsAsMap(new File(f))
        then:

        items.size() > 0
        def aloeVera = items["Aloe Vera_1000"]
        aloeVera.itemQuantity == 1000
        aloeVera.itemTax == 15
        aloeVera.itemPrice == 0.239

        def zazvor = items["Zázvor v hořké čokoládě - Anglie_3000"]
        zazvor.itemQuantity == 3000
        zazvor.itemTax == 15
        zazvor.itemPrice == 0.121

        def pinie = items["Piniové oříšky_1000"]
        pinie.itemQuantity == 1000
        pinie.itemTax == 15
        pinie.itemPrice == 0.732


    }

    def "Make Order"() {
        given:
        def processor = new NutSheetProcessor()
        def workbook = processor
        def sheetRead = fillWriteAndReadSheet(processor)
        def sheetWithOrder = sheetRead.getWorkbook().getSheet("Objednávka")

        expect:
        sheetWithOrder.getRow(3).getCell(5).getNumericCellValue() == 3
        sheetWithOrder.getRow(46).getCell(5).getNumericCellValue() == 1

    }
}
