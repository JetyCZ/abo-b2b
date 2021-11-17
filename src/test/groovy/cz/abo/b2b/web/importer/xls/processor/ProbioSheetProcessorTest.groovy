package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.importer.dto.ImportSource


class ProbioSheetProcessorTest extends AbstractSheetProcessorTest {

    @Override
    protected String getPricelistResourcePath() {
        return  "/190901_cenik_PROBIO_zari_rijen_2019.xls"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new ProbioSheetProcessor().parseItemsAsMap(ImportSource.fromFile(f))
        then:

        items.size() > 0
        def item1 = items["Pšenice červená  3 kg BIOHARMONIE_3000"]
        item1.itemQuantity == 3000
        item1.itemTax == 15
        item1.itemPrice == 0.1137

        def item2 = items["Semínka slunečnicová  2 kg BIOHARMONIE_2000"]
        item2.itemQuantity == 2000
        item2.itemTax == 15
        item2.itemPrice == 0.156
    }

    def "Make Order"() {
        given:
        def sheetRead = fillWriteAndReadSheet(new ProbioSheetProcessor())

        expect:
        sheetRead.getRow(4).getCell(14).getNumericCellValue() == 3
        sheetRead.getRow(7).getCell(14).getNumericCellValue() == 1

    }
}
