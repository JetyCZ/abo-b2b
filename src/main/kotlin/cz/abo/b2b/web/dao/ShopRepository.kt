package cz.abo.b2b.web.dao

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ShopRepository : JpaRepository<Shop?, Long?> {
}