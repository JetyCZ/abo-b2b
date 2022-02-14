package cz.abo.b2b.web.state.shoppingcart

import cz.abo.b2b.web.dao.Product
import java.math.BigDecimal
import java.math.RoundingMode

class ShoppingCartItem (val product: Product, var count: Long) {
    fun totalPriceNoVAT(): BigDecimal {
        return product.priceNoVAT.multiply(BigDecimal(count)).multiply(product.quantity).stripTrailingZeros()
    }
    fun totalPriceVAT(): BigDecimal {
        return totalPriceNoVAT().multiply(BigDecimal(1+product.VAT)).stripTrailingZeros()
    }
}