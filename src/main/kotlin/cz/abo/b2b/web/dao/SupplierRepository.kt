package cz.abo.b2b.web.dao

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SupplierRepository : JpaRepository<Supplier?, UUID?> {
    fun findByNameContaining(name: String?): List<Supplier?>?

}