package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.UnitEnum
import cz.abo.b2b.web.importer.dto.ImportSource

class KServisSheetProcessorTest extends AbstractSheetProcessorTest {


    @Override
    protected String getPricelistResourcePath() {
        return  "/k-servis/2023_11_k-servis_ceník_listopad.xlsx"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new KServisSheetProcessor().parseProductsAsMap(
                ImportSource.fromFile(f), testSupplier
        )
        then:

        items.size() > 0
        def item2 = items["Aloe Vera plátky (SUŠENÉ OVOCE)_20"]
        item2.getVAT() == 0.15
        item2.getPriceNoVAT() == 204
        item2.rowNum == 1


        def item1 = items["Ananas kostky 8-10mm (SUŠENÉ OVOCE)_20"]
        item1.VAT == 0.15
        item1.priceNoVAT == 109
        item1.rowNum == 4

        def item3 = items["Rozinková (OVOCNÉ PASTY)_10"]
        item3.VAT == 0.15
        item3.priceNoVAT == 53
        item3.unit == UnitEnum.KG
        item3.quantity == 10




    }

    def "Make Order"() {
        given:
        def sheetRead = fillWriteAndReadSheet(new KServisSheetProcessor(), 102)

        expect:
        sheetRead.getRow(1).getCell(6).getNumericCellValue() == 3
        sheetRead.getRow(4).getCell(6).getNumericCellValue() == 1
        sheetRead.getRow(117).getCell(6).getNumericCellValue() == 1
        sheetRead.getRow(0).getCell(6).getStringCellValue() == "Objednávám tolik balení"
    }
}
