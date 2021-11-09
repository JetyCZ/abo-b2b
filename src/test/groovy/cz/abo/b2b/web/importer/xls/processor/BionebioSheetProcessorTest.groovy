package cz.abo.b2b.web.importer.xls.processor



class BionebioSheetProcessorTest extends AbstractSheetProcessorTest {

    @Override
    protected String getPricelistResourcePath() {
        return  "/bionebio/OL_bio nebio_11_2021.xls"
    }

    def "IterateSheetValues"() {
        def f = resourceFilePath(getPricelistResourcePath())

        when:
        def items = new BionebioSheetProcessor().parseItemsAsMap(new File(f))
        then:

        items.size() > 0
        def item1 = items["Přírodní třtinový cukr  SUROVÝ MU_50000"]
        item1.itemQuantity == 50000
        item1.itemTax == 15
        item1.itemPrice == 0.0317

        def item2 = items["Bio kypřící prášek z vinného kamene bio*nebio_4000"]
        item2.itemQuantity == 4000
        item2.itemTax == 15
        item2.itemPrice == 0.545

    }

    def "Make Order"() {
        given:
        def sheetRead = fillWriteAndReadSheet(new BionebioSheetProcessor())

        expect:
        sheetRead.getRow(5).getCell(4).getNumericCellValue() == 3
        sheetRead.getRow(8).getCell(4).getNumericCellValue() == 1

    }


}
