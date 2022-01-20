package cz.abo.b2b.web.importer

import cz.abo.b2b.web.SystemUtils
import cz.abo.b2b.web.dao.ProductRepository
import cz.abo.b2b.web.dao.SupplierRepository
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
    val heurekaXMLParser: HeurekaXMLParser,
    val applicationContext: ApplicationContext):InitializingBean {

    companion object {
        val LOGGER = LoggerFactory.getLogger(SuppliersImport::class.java)
    }

    fun importAll() {
        supplierRepository.deleteAll()
        productRepository.deleteAll()

        for (supplier in suppliers.suppliers()) {
            LOGGER.info("XXX BEFORE: " + supplier.name + "; " + SystemUtils.usedMemory())
            if (!StringUtils.isEmpty(supplier.importUrl)) {
                val saved = supplierRepository.save(supplier)

                val importerClass = Class.forName(supplier.importerClassName)
                val importer = applicationContext.getBean(importerClass)
                val importSource = supplier.importSource()
                val products = (importer as AbstractSheetProcessor).parseProductsWithSupplier(saved, importSource)
                productRepository.saveAll(products)

            }
            LOGGER.info("XXX AFTER: " + supplier.name + "; " + SystemUtils.usedMemory())
        }
    }

    override fun afterPropertiesSet() {
        importAll()
    }


}