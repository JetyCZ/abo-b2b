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
        def items = new KServisSheetProcessor().parseProductsAsMap(
                ImportSource.fromFile(f), testSupplier
        )
        then:

        items.size() > 0
        def item2 = items["Aloe Vera plátky (SUŠENÉ OVOCE)_20000"]
        item2.getVAT() == 0.15
        item2.getPriceNoVAT() == 0.204
        item2.rowNum == 1


        def item1 = items["Ananas kostky 8-10mm (SUŠENÉ OVOCE)_20000"]
        item1.VAT == 0.15
        item1.priceNoVAT == 0.109
        item1.rowNum == 4

        def item3 = items["Rozinková (OVOCNÉ PASTY)_10000"]
        item3.VAT == 0.15
        item3.priceNoVAT == 0.053




    }

    def "Make Order"() {
        given:
        def sheetRead = fillWriteAndReadSheet(new KServisSheetProcessor(), 102)

        expect:
        sheetRead.getRow(1).getCell(6).getNumericCellValue() == 3
        sheetRead.getRow(4).getCell(6).getNumericCellValue() == 1
        sheetRead.getRow(120).getCell(6).getNumericCellValue() == 1
        sheetRead.getRow(0).getCell(6).getStringCellValue() == "Objednávám tolik balení"
    }
}
