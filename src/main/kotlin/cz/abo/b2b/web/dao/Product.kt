package cz.abo.b2b.web.dao

import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
class Product(

    var productName: String,

    var priceVAT: BigDecimal,

    @Column(columnDefinition="LONGTEXT")
    var description: String?,

    @ManyToOne
    var supplier: Supplier

    ) {

    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID()

}