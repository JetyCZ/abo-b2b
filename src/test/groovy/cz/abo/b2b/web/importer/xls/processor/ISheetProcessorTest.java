package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.importer.xls.dto.Item;
import org.junit.Test;

import static org.junit.Assert.*;

public class ISheetProcessorTest {
    private ISheetProcessor countrySheetProcessor = new CountrySheetProcessor();

    @Test
    public void itemIsNotValidatedTest() {
        Item item = new Item("", 0.0, 0.0, 0);
        assertFalse(countrySheetProcessor.validateImportedObject(item));
    }

    @Test
    public void itemIsValidatedTest() {
        Item item = new Item("orech", 2000.0, 256.0, 25);
        assertTrue(countrySheetProcessor.validateImportedObject(item));
    }

    @Test
    public void countValueForOneGramTestOkay() {
        assertEquals(4.0, countrySheetProcessor.countValueForOneGram(800.0, 200.0), 2);
    }
}