package cz.abo.b2b.web.dao

import cz.abo.b2b.web.view.component.ViewUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
class Product {
    constructor(
        productName: String,

        /**
         * Price for one unit of this product. If this product is added to shopping cart,
         * it's cost is priceVAT * quantity
         */
        priceNoVAT: BigDecimal,

        VAT: Double,

        description: String?,

        /**
         * How many units are in the packaging of this product
         */
        quantity: BigDecimal,

        /**
         * Units that this product contains
         */
        unit: UnitEnum,
        ean: String?,
        supplier: Supplier
    ) {
        this.productName = productName
        this.priceNoVAT = priceNoVAT.setScale(5, RoundingMode.HALF_UP).stripTrailingZeros()
        this.VAT = VAT
        this.description = description
        this.quantity = quantity.setScale(5, RoundingMode.HALF_UP).stripTrailingZeros()
        this.unit = unit
        this.ean = ean
        this.supplier = supplier
    }

    var supplierCode: String? = null
    var productName: String

    /**
     * Price for one unit of this product. If this product is added to shopping cart,
     * it's cost is priceVAT * quantity
     */
    var priceNoVAT: BigDecimal

    var VAT: Double

    @Column(columnDefinition = "LONGTEXT")
    var description: String?

    /**
     * How many units are in the packaging of this product
     */
    var quantity: BigDecimal

    /**
     * Units that this product contains
     */
    var unit: UnitEnum
    var ean: String?

    @ManyToOne
    var supplier: Supplier

    var rowNum: Int = 0
    var bestBefore: LocalDate? = null
    var parseIdx: Int = 0

    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID()

    fun priceVAT(quantity: Int):BigDecimal {
        return priceVAT().multiply(BigDecimal(quantity))
    }
    fun priceNoVAT(quantity: Int):BigDecimal {
        return priceNoVAT.multiply(BigDecimal(quantity))
    }

    fun priceVAT() : BigDecimal {
        return priceNoVAT.multiply(BigDecimal(1 + VAT))
    }

}