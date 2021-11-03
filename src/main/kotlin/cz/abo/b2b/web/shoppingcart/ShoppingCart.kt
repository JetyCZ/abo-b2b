package cz.abo.b2b.web.shoppingcart

import cz.abo.b2b.web.dao.Product
import java.util.*
import kotlin.collections.HashMap

open class ShoppingCart : HashMap<UUID, ShoppingCartItem>() {
    fun add(product: Product, count: Long) {
        val item = getOrDefault(product.id, ShoppingCartItem(product, 0L))
        item.count += count
        put(product.id, item)
    }
}