package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.OrderAttachment
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import static org.junit.Assert.*;

class DefaultAbstractSheetProcessorTest {
    private AbstractSheetProcessor abstractSheetProcessor = new AbstractSheetProcessor() {
        @Override
        List<Product> parseProducts(@NotNull ImportSource importSource, @NotNull Supplier supplier) {
            return null
        }

        @Override
        OrderAttachment fillOrder(@NotNull File fileWithOrderAttachment, @NotNull Map<Product, Integer> orderedProducts) {
            return null
        }
    }

    @Test
    void itemIsNotValidatedTest() {
        Product product = new Product();
        product.setProductName("");
        product.setQuantity(BigDecimal.ZERO);
        product.setPriceNoVAT(BigDecimal.ZERO);
        product.setVAT(0);
        assertFalse(abstractSheetProcessor.validateImportedObject(product));
    }

    @Test
    void itemIsValidatedTest() {
        Product product = new Product();
        product.setProductName("orech");
        product.setQuantity(new BigDecimal(2000));
        product.setPriceNoVAT(new BigDecimal(256));
        product.setVAT(0.15);
        assertTrue(abstractSheetProcessor.validateImportedObject(product));
    }

    @Test
    void countValueForOneGramTestOkay() {
        assertEquals(4.0, abstractSheetProcessor.countValueForOneGram(800.0, 200.0), 2);
    }
}