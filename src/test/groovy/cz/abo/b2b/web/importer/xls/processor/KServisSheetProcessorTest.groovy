package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.importer.dto.ImportSource

class KServisSheetProcessorTest extends AbstractSheetProcessorTest {


    @Override
    protected String getPricelistResourcePath() {
        return  "/k-servis/cenik_srpen.xlsx"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new KServisSheetProcessor().parseItemsAsMap(
                ImportSource.fromFile(f)
        )
        then:

        items.size() > 0
        def item2 = items["Aloe Vera plátky (SUŠENÉ OVOCE)_20000"]
        item2.itemTax == 15
        item2.itemPrice == 0.204
        item2.rowNum == 1


        def item1 = items["Ananas kostky 8-10mm (SUŠENÉ OVOCE)_20000"]
        item1.itemTax == 15
        item1.itemPrice == 0.109
        item1.rowNum == 4

        def item3 = items["Rozinková (OVOCNÉ PASTY)_10000"]
        item3.itemTax == 15
        item3.itemPrice == 0.053




    }

    def "Make Order"() {
        given:
        def sheetRead = fillWriteAndReadSheet(new KServisSheetProcessor(), 102)

        expect:
        sheetRead.getRow(1).getCell(5).getNumericCellValue() == 3
        sheetRead.getRow(4).getCell(5).getNumericCellValue() == 1
        sheetRead.getRow(142).getCell(5).getNumericCellValue() == 1
        sheetRead.getRow(0).getCell(5).getStringCellValue() == "Objednávám tolik balení"

    }
}
