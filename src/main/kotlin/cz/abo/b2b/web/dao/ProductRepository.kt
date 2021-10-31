package cz.abo.b2b.web.dao

import cz.abo.b2b.web.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product?, Long?> {

}