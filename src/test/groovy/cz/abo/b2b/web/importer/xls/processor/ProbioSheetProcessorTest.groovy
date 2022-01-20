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
        def items = new ProbioSheetProcessor().parseProductsAsMap(
                ImportSource.fromFile(f), testSupplier
        )
        then:

        items.size() > 0
        def item1 = items["Pšenice červená  3 kg BIOHARMONIE_3"]
        item1.quantity == 3
        item1.VAT == 0.15
        item1.priceNoVAT == 113.7

        def item2 = items["Semínka slunečnicová  2 kg BIOHARMONIE_2"]
        item2.quantity == 2
        item2.VAT == 0.15
        item2.priceNoVAT == 156
    }

    def "Make Order"() {
        given:
        def sheetRead = fillWriteAndReadSheet(new ProbioSheetProcessor())

        expect:
        sheetRead.getRow(4).getCell(14).getNumericCellValue() == 3
        sheetRead.getRow(7).getCell(14).getNumericCellValue() == 1

    }
}
