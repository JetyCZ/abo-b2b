package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.UnitEnum
import cz.abo.b2b.web.importer.dto.ImportSource


class BionebioSheetProcessorTest extends AbstractSheetProcessorTest {

    @Override
    protected String getPricelistResourcePath() {
        return  "/bionebio/OL_bio_nebio_11_2021.xls"
    }

    def "IterateSheetValues"() {
        def f = resourceFilePath(getPricelistResourcePath())

        when:
        def items = new BionebioSheetProcessor().parseProductsAsMap(ImportSource.fromFile(f), testSupplier)
        then:

        items.size() > 0
        def item1 = items["Přírodní třtinový cukr  SUROVÝ MU_50"]
        item1.quantity == 50
        item1.VAT == 0.15
        item1.unit == UnitEnum.KG
        item1.priceNoVAT == 31.7
        def item2 = items["Přírodní třtinový cukr SUROVÝ bio*nebio_4"]
        item2.quantity == 4
        item2.VAT == 0.15
        item2.unit == UnitEnum.KG
        item2.priceNoVAT == 112/4

        def item3 = items["Bio kypřící prášek z vinného kamene bio*nebio_4"]
        item3.quantity == 4
        item3.VAT == 0.15
        item2.unit == UnitEnum.KG
        item3.priceNoVAT == 545/4


    }

    def "Make Order"() {
        given:
        def sheetRead = fillWriteAndReadSheet(new BionebioSheetProcessor())

        expect:
        sheetRead.getRow(5).getCell(4).getNumericCellValue() == 3
        sheetRead.getRow(8).getCell(4).getNumericCellValue() == 1

    }


}
