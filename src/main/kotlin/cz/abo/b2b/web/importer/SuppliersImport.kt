package cz.abo.b2b.web.importer

import cz.abo.b2b.web.SystemUtils
import cz.abo.b2b.web.dao.ProductRepository
import cz.abo.b2b.web.dao.SupplierRepository
import cz.abo.b2b.web.importer.impl.HeurekaXMLParser
import cz.abo.b2b.web.importer.xls.processor.AbstractSheetProcessor
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
open class SuppliersImport(
    val productRepository: ProductRepository,
    val supplierRepository: SupplierRepository,
    val suppliers: Suppliers,
    val HeurekaXMLParser: HeurekaXMLParser,
    val applicationContext: ApplicationContext):InitializingBean {

    companion object {
        val LOGGER = LoggerFactory.getLogger(SuppliersImport::class.java)
    }

    fun importAll() {
        productRepository.deleteAll()
        supplierRepository.deleteAll()

        val suppliersToImport = suppliers.suppliers()
        for (supplier in suppliersToImport) {
            LOGGER.info("XXX BEFORE: " + supplier.name + "; " + SystemUtils.usedMemory())
            val saved = supplierRepository.save(supplier)

            val importerClass = Class.forName(supplier.importerClassName)
            val importer = applicationContext.getBean(importerClass)
            val importSource = supplier.importSource()
            val products = (importer as AbstractSheetProcessor).parseProducts(importSource, saved)
            productRepository.saveAll(products)

            LOGGER.info("XXX AFTER: " + supplier.name + "; " + SystemUtils.usedMemory())
        }


    }

    override fun afterPropertiesSet() {
        importAll()
    }


}