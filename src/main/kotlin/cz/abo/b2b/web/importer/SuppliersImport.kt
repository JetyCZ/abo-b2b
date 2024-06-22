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
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

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

    @Scheduled(cron = "0 0 4 * * ?")
    fun importAll() {
        productRepository.deleteAll()
        supplierRepository.deleteAll()

        val suppliersToImport = suppliers.suppliers()
        for (supplier in suppliersToImport) {
            val saved = supplierRepository.save(supplier)

            val importerClass = Class.forName(supplier.importerClassName)
            val importer = applicationContext.getBean(importerClass)
            val importSource = supplier.importSource()
            try {
                val products = (importer as AbstractSheetProcessor).parseProducts(importSource, saved)
                productRepository.saveAll(products)
                LOGGER.info("XXX Import ok: " + products.size)
                supplier.lastImport = LocalDateTime.now()
            } catch (e: Exception) {
                LOGGER.error("XXX Import failed: " + supplier.name, e)
            }
        }


    }

    override fun afterPropertiesSet() {
        importAll()
    }


}
