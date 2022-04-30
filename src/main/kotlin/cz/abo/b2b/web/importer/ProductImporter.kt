package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.importer.dto.ImportSource

interface ProductImporter {
    fun parseProducts(importSource: ImportSource, supplier: Supplier): List<Product>

}
