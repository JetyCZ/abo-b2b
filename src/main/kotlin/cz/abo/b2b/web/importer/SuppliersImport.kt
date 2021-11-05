package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.ProductRepository
import cz.abo.b2b.web.dao.SupplierRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Component
class SuppliersImport(val productRepository: ProductRepository, val supplierRepository: SupplierRepository, val suppliers: Suppliers, val heurekaXMLParser: HeurekaXMLParser) {

    fun importAll() {
        supplierRepository.deleteAll()
        for (supplier in suppliers.suppliers()) {
            if (!StringUtils.isEmpty(supplier.importUrl)) {

                var urlStream = URL(supplier.importUrl).openStream()
                var tempFilePath =  System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID()
                Files.copy(urlStream, Paths.get(tempFilePath));

                val saved = supplierRepository.save(supplier)
                val tempFile = File(tempFilePath)
                val products = heurekaXMLParser.parseStream(tempFile, saved)
                tempFile.delete()

                productRepository.saveAll(products)

            }
        }
    }
}