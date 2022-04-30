package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Shop
import cz.abo.b2b.web.dao.UnitEnum
import cz.abo.b2b.web.dao.User
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.security.SecurityService
import org.junit.runner.RunWith
import spock.lang.Ignore


class CalthaSheetProcessorTest extends AbstractSheetProcessorTest {


    @Override
    protected String getPricelistResourcePath() {
        return  "/caltha/2021-10_Objednavkovy_formular_CALTHA.xlsx"
    }

    def "IterateSheetValues"() {
        def f = getClass().getResource(getPricelistResourcePath()).getFile()

        when:
        def items = new CalthaSheetProcessor().parseProductsAsMap(
                ImportSource.fromFile(f), testSupplier
        )
        then:

        def item2 = items["Badyán s černým uhlím (MÝDLO)_1"]
        item2.getVAT() == 0.15
        item2.getPriceNoVAT() == 59
        item2.rowNum == 7
        item2.ean == "8594196840011"
        def item_1 = items["Dětské s měsíčkem lékařským (atest pro děti do 3 let) (TEKUTÉ MÝDLO)_1"]
        item_1.priceNoVAT.toDouble() == 380
        item_1.unit == UnitEnum.KG
        def item_5 = items["Dětské s měsíčkem lékařským (atest pro děti do 3 let) (TEKUTÉ MÝDLO)_5"]
        item_5.priceNoVAT.toDouble() == 300
        item_5.unit == UnitEnum.KG
        def item_10 = items["Dětské s měsíčkem lékařským (atest pro děti do 3 let) (TEKUTÉ MÝDLO)_10"]
        item_10.priceNoVAT.toDouble() == 280
        item_10.unit == UnitEnum.KG

        def mydl = items["Mýdlenka dřevěná (OSTATNÍ)_1"]
        mydl.priceNoVAT.toDouble() == 70
        mydl.unit == UnitEnum.KS

    }


    SecurityService securityService = Mock()
    @Ignore
    def "Make Order"() {
        def processor = new CalthaSheetProcessor()

        processor.securityService = securityService

        given:
        def sheetRead = fillWriteAndReadSheet(processor, null)

        expect:
        sheetRead.getRow(1).getCell(6).getNumericCellValue() == 3
        sheetRead.getRow(4).getCell(6).getNumericCellValue() == 1
        sheetRead.getRow(117).getCell(6).getNumericCellValue() == 1
        sheetRead.getRow(0).getCell(6).getStringCellValue() == "Objednávám tolik balení"
    }
}
