package cz.abo.b2b.web.dao

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
class Product(

    var productName: String,

    var priceVAT: BigDecimal,
    var VAT: Double,

    @Column(columnDefinition = "LONGTEXT")
    var description: String?,

    var quantity: BigDecimal,
    var ean: String?,
    @ManyToOne
    var supplier: Supplier

) {

    var rowNum: Int = 0
    var bestBefore: LocalDate? = null

    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID()


    fun priceNoVAT() : BigDecimal {
        return priceVAT.divide(BigDecimal(1 + VAT), 2, RoundingMode.HALF_UP)
    }


}