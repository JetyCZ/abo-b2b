package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.importer.dto.ImportSource


class CountryLifeSheetProcessorTest extends AbstractSheetProcessorTest {


    @Override
    protected String getPricelistResourcePath() {
        return  "/CountryLife_Objednavkovy_cenik_VO.xls"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new CountrySheetProcessor().parseItemsAsMap(ImportSource.fromFile(f))
        then:

        items.size() > 0
        def jahlyBio = items["Jáhly   COUNTRY LIFE_5000_BIO"]
        jahlyBio.itemQuantity == 5000
        jahlyBio.itemTax == 15
        jahlyBio.itemPrice == 0.0462
        def jahly = items["Jáhly   COUNTRY LIFE_5000"]
        jahly.itemQuantity == 5000
        jahly.itemTax == 15
        jahly.itemPrice == 0.0349
        def merunky = items["Meruňky sušené_12500_BIO"]
        merunky.itemQuantity == 12500
        merunky.itemTax == 15
        merunky.itemPrice == 0.129
    }

    def "Make Order"() {
        given:
        def sheetRead = fillWriteAndReadSheet(new CountrySheetProcessor())

        expect:
        sheetRead.getRow(13).getCell(22).getNumericCellValue() == 3
        sheetRead.getRow(19).getCell(22).getNumericCellValue() == 1

    }
}
