package cz.abo.b2b.web.importer

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.ProductRepository
import cz.abo.b2b.web.dao.SupplierRepository
import cz.abo.b2b.web.importer.xls.processor.AbstractSheetProcessor
import org.apache.commons.lang3.StringUtils
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.io.File
import java.math.BigDecimal
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

@Component
class SuppliersImport(
    val productRepository: ProductRepository,
    val supplierRepository: SupplierRepository,
    val suppliers: Suppliers,
    val heurekaXMLParser: HeurekaXMLParser,
    val applicationContext: ApplicationContext) {



    fun importAll() {
        supplierRepository.deleteAll()
        productRepository.deleteAll()

        for (supplier in suppliers.suppliers()) {
            if (!StringUtils.isEmpty(supplier.importUrl)) {
                val saved = supplierRepository.save(supplier)

                val products: MutableList<Product>
                if (supplier.importUrl.startsWith("http")) {
                    var urlStream = URL(supplier.importUrl).openStream()
                    var tempFilePath =  System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID()
                    Files.copy(urlStream, Paths.get(tempFilePath));

                    val tempFile = File(tempFilePath)
                    products = heurekaXMLParser.parseStream(tempFile, saved)
                    tempFile.delete()

                } else {
                    val importerClass = Class.forName(supplier.importerClassName)
                    val importer = applicationContext.getBean(importerClass)
                    val fileToParse = resourceFilePath(supplier.importUrl)
                    val items = (importer as AbstractSheetProcessor).parseItems(File(fileToParse))
                    products = ArrayList()
                    for (item in items) {
                        val VAT = 0.01 * item.itemTax
                        val priceVAT = BigDecimal((1 + VAT) * item.itemPrice)
                        val quantity = BigDecimal(item.itemQuantity)
                        products.add(
                            // TODO item.description
                            Product(item.itemName, priceVAT, VAT, "", quantity, saved)
                        )
                    }
                }
                productRepository.saveAll(products)

            }
        }
    }

    fun resourceFilePath(relativeResourcesPath: String) : String{
        return SuppliersImport::class.java.getResource(relativeResourcesPath).getFile().replace("%20", " ")
    }
}