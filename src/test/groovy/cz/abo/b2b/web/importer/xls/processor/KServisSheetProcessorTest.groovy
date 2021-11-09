package cz.abo.b2b.web.importer.xls.processor

class KServisSheetProcessorTest extends AbstractSheetProcessorTest {


    @Override
    protected String getPricelistResourcePath() {
        return  "/k-servis/k-servis_cenik_brezen_2020.xlsx"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new KServisSheetProcessor().parseItemsAsMap(new File(f))
        then:

        items.size() > 0
        def itemPara = items["Para ořechy_20000"]
        itemPara.itemTax == 15
        itemPara.itemPrice == 0.198
        itemPara.rowNum == 149


        def item1 = items["Aloe Vera plátky_20000"]
        item1.itemTax == 15
        item1.itemPrice == 0.204
        item1.rowNum == 1

        def item2 = items["Zelený meloun plátky_20000"]
        item2.itemTax == 15
        item2.itemPrice == 0.159

        def item3 = items["Konopné semínko loupané_10000"]
        item3.itemTax == 15
        item3.itemPrice == 0.189
        item3.rowNum == 394

        def item4 = items["Zahrádka (směs ovoce)_12000"]
        item4.itemTax == 15
        item4.itemPrice == 0.087


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
