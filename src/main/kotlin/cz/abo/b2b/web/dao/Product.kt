package cz.abo.b2b.web

import java.math.BigDecimal
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Product(var productName: String, var priceVAT: BigDecimal, var description: String?) {
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID()

}