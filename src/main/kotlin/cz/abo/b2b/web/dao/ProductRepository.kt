package cz.abo.b2b.web.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product?, Long?> {
    fun findByProductNameContainingIgnoreCase(productName: String?): List<Product?>?

    @Query("SELECT p FROM Product p WHERE " +
            "lower(p.productName) like lower(concat('%', :productName, '%'))" +
            " and (p.supplier.id IN :supplierIds)"
            )
    fun find(@Param("productName") productName: String, @Param("supplierIds") supplierIds: List<Long>): List<Product>

    @Query("SELECT p FROM Product p WHERE " +
            "lower(p.productName) like lower(concat('%', :productName, '%'))"
            )
    fun find(@Param("productName") productName: String): List<Product>
}
