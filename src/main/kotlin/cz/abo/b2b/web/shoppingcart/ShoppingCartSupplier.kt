package cz.abo.b2b.web.shoppingcart

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Supplier
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.HashMap

class ShoppingCartSupplier (val supplier: Supplier) : HashMap<UUID, ShoppingCartItem>() {
    fun addToCart(product: Product, count: Long) {
        val item = getOrDefault(product.id, ShoppingCartItem(product, 0L))
        item.count += count
        put(product.id, item)
    }

    fun totalPriceNoVAT() : BigDecimal {
        var result : BigDecimal = BigDecimal.ZERO
        for (shoppingCartItem in values) {
            val withoutVATMultiplier = BigDecimal(1 - shoppingCartItem.product.VAT)
            result = result.add(
                shoppingCartItem.product.priceVAT
                    .multiply(BigDecimal(shoppingCartItem.count)
                    .multiply(withoutVATMultiplier))
            )
        }
        return result.setScale(2, RoundingMode.HALF_UP)
    }

    fun totalPriceVAT() : BigDecimal {
        var result : BigDecimal = BigDecimal.ZERO
        for (shoppingCartItem in values) {
            result = result.add(shoppingCartItem.product.priceVAT.multiply(BigDecimal(shoppingCartItem.count)))
        }
        return result.setScale(2, RoundingMode.HALF_UP)
    }

    fun remainingToFreeTransportNoVAT() : BigDecimal {
        val remaining = supplier.freeTransportFrom.minus(totalPriceNoVAT())
        return remaining.setScale(2, RoundingMode.HALF_UP)
    }
}