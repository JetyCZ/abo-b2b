package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.importer.dto.ImportSource

class MKMPackSheetProcessorTest extends AbstractSheetProcessorTest {

    @Override
    protected String getPricelistResourcePath() {
        return  "/mkm/iPlody_2025_01.xlsx"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new MkmPackSheetProcessor().parseProductsAsMap(
                ImportSource.fromFile(f), testSupplier
        )
        then:

        items.size() > 0
        def amarantZrno = items["Amarant zrno (OBILOVINY)_5"]
        amarantZrno.quantity == 5
        amarantZrno.VAT == 0.15
        amarantZrno.priceNoVAT == 45.1

        def spalda = items["Špalda pufovaná (PUFOVANÉ)_1"]
        spalda.quantity == 1
        spalda.VAT == 0.15
        spalda.priceNoVAT == 35.9



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
