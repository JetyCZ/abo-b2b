package cz.abo.b2b.web.dao

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product?, Long?> {
    fun findByProductNameContaining(productName: String?): List<Product?>?

}