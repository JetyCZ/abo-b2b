package cz.abo.b2b.web.dao

import cz.abo.b2b.web.importer.xls.processor.NetikSheetProcessor
import cz.abo.b2b.web.importer.xls.processor.WolfberrySheetProcessor
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal

@SpringBootTest(classes = [JdbcDaoSpringConfig::class])
@AutoConfigureTestDatabase
@AutoConfigureDataJpa
open class ProductRepositoryTest()  {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var supplierRepository: SupplierRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Test
    fun supplierDetails() {
        val wolfberry = supplierRepository.save(
            Supplier(
                "Wolfberry", BigDecimal(2000), "", "http://cup.wolfberry.cz/xml-export/bezobalu_cz.xml",
                WolfberrySheetProcessor::class.qualifiedName!!, "objednavky@wolfberry.cz"
            )
        )
        val netik = Supplier("BIODVŮR Jaroslav Netík", BigDecimal.ZERO, "","",
            NetikSheetProcessor::class.qualifiedName!!, "jaroslav.netik@email.cz"
        )
        supplierRepository.saveAll(
            listOf(wolfberry, netik)
        )
        productRepository.save(
            Product("wolfeberry kešu", BigDecimal(0.15), 0.15, "",BigDecimal(0), UnitEnum.KG, "EAN", wolfberry )
        )
        productRepository.save(
            Product("netik kešu", BigDecimal(0.15), 0.15, "",BigDecimal(0), UnitEnum.KG, "EAN2", netik )
        )
        assertEquals(1, productRepository.find("keš", listOf(netik.id)).size)
        assertEquals(2, productRepository.find("keš", listOf(netik.id, wolfberry.id)).size)
        assertEquals(0, productRepository.find("xxx", listOf(netik.id, wolfberry.id)).size)
    }
}