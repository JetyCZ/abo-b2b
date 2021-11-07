package cz.abo.b2b.web.shoppingcart

import cz.abo.b2b.web.dao.Product
import java.util.*
import kotlin.collections.HashMap

open class ShoppingCart : HashMap<UUID, ShoppingCartSupplier>() {
    fun add(product: Product, count: Long) {
        val shoppingCartSupplier = getOrDefault(product.supplier.id, ShoppingCartSupplier(product.supplier))
        put(product.supplier.id, shoppingCartSupplier)
        shoppingCartSupplier.addToCart(product, count)
    }
}