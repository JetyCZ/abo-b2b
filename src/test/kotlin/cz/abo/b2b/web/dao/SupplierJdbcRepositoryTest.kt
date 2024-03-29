package cz.abo.b2b.web.dao

import cz.abo.b2b.web.dao.jdbc.SupplierJdbcRepository
import cz.abo.b2b.web.importer.xls.processor.WolfberrySheetProcessor
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal

@SpringBootTest(classes = [JdbcDaoSpringConfig::class])
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureDataJpa
open class SupplierJdbcRepositoryTest()  {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var supplierRepository: SupplierRepository

    @Autowired
    lateinit var productRepository: ProductRepository


    lateinit var supplierJdbcRepository: SupplierJdbcRepository

    @BeforeEach
    fun before() {
        supplierJdbcRepository = SupplierJdbcRepository(jdbcTemplate)
        productRepository.deleteAll()
        supplierRepository.deleteAll()
    }


    @Test
    fun supplierDetails() {
        val supplier = Supplier(
            "Wolfberry", BigDecimal(2000), ArrayList(), "", "http://cup.wolfberry.cz/xml-export/bezobalu_cz.xml",
            WolfberrySheetProcessor::class.qualifiedName!!, "objednavky@wolfberry.cz"
        )
        val saved = supplierRepository.save(
            supplier
        )
        productRepository.save(
            Product("aaa", BigDecimal(0.15), 0.15, "",BigDecimal(0), UnitEnum.KG, "EAN", saved )
        )
        productRepository.save(
            Product("bbb", BigDecimal(0.15), 0.15, "",BigDecimal(0), UnitEnum.KG, "EAN2", saved )
        )
        val supplierDetails = supplierJdbcRepository.supplierDetails()
        assertEquals(1, supplierDetails.size)

        assertEquals("Wolfberry", supplierDetails.get(0).name)
        assertEquals(2, supplierDetails.get(0).productCount)
    }
}
