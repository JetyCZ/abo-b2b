package cz.abo.b2b.web.importer.xls.processor

import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.googlesheet.GoogleSheetParser
import org.assertj.core.util.Lists
import org.junit.jupiter.api.Test

class ZeleninaRokytnoSheetProcessorTest {

    @Test
    parseProducts() {
        ZeleninaRokytnoSheetProcessor processor = new ZeleninaRokytnoSheetProcessor(
            googleSheetParser: new GoogleSheetParser()
        )
        processor.parseProducts(
            new ImportSource(),
            new Supplier("test",BigDecimal.ZERO, Lists.emptyList(), "","63fc83e3a0a10310b45f4d57","","" )
        )
    }
}
