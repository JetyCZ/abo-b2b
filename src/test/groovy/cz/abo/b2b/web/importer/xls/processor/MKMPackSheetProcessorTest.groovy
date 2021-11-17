package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.importer.dto.ImportSource

class MKMPackSheetProcessorTest extends AbstractSheetProcessorTest {

    @Override
    protected String getPricelistResourcePath() {
        return  "/mkm/MKM_pack_B_cenik_SUROVINY_unor_2020.xlsx"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new MkmPackSheetProcessor().parseItemsAsMap(ImportSource.fromFile(f))
        then:

        items.size() > 0
        def amarantZrno = items["Amarant zrno (OBILOVINY)_5000"]
        amarantZrno.itemQuantity == 5000
        amarantZrno.itemTax == 15
        amarantZrno.itemPrice == 0.0451

        def spalda = items["Špalda pufovaná (PUFOVANÉ)_1000"]
        spalda.itemQuantity == 1000
        spalda.itemTax == 15
        spalda.itemPrice == 0.0359



    }

    def "Make Order"() {
        given:
        def processor = new MkmPackSheetProcessor()
        def sheetRead = fillWriteAndReadSheet(processor)
        def workbook = sheetRead.getWorkbook()

        expect:
        processor.getOrderedQuantity(workbook, 9) == 3
        processor.getOrderedQuantity(workbook, 12) == 1

    }
}
