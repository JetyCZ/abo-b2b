package cz.abo.b2b.web.state.shoppingcart

import cz.abo.b2b.web.dao.Product
import java.util.*
import kotlin.collections.HashMap

open class ShoppingCart : HashMap<UUID, ShoppingCartSupplier>() {
    fun add(product: Product, count: Long) {
        val shoppingCartSupplier = shoppingCartSupplier(product)
        shoppingCartSupplier.addToCart(product, count)
    }

    fun update(product: Product, newCount: Double?) {
        if (newCount!=null) {
            val shoppingCartSupplier = shoppingCartSupplier(product)
            shoppingCartSupplier.updateCart(product, newCount)
        }
    }

    private fun shoppingCartSupplier(product: Product): ShoppingCartSupplier {
        val shoppingCartSupplier = getOrDefault(product.supplier.id, ShoppingCartSupplier(product.supplier))
        put(product.supplier.id, shoppingCartSupplier)
        return shoppingCartSupplier
    }
}