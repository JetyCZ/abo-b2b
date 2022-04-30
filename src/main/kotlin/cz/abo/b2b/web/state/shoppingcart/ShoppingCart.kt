package cz.abo.b2b.web.state.shoppingcart

import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.Shop
import cz.abo.b2b.web.importer.xls.service.ProcessorService
import java.util.*
import kotlin.collections.HashMap

/**
 * Map, where key is id of supplier and value is ShoppingCartSupplier (items bought for one supplier)
 */
open class ShoppingCart : HashMap<Long, ShoppingCartSupplier>() {
    fun add(processorService: ProcessorService, product: Product, count: Long, shop: Shop) {
        val shoppingCartSupplier = shoppingCartSupplier(processorService, product, shop)
        shoppingCartSupplier.addToCart(product, count)
    }

    fun update(processorService: ProcessorService, product: Product, newCount: Double?, shop: Shop) {
        if (newCount!=null) {
            val shoppingCartSupplier = shoppingCartSupplier(processorService, product, shop)
            shoppingCartSupplier.updateCart(product, newCount)
            var isEmpty = true;
            for (item in shoppingCartSupplier.values) {
                if (item.count>0) isEmpty = false;
            }
            if (isEmpty) {
                remove(shoppingCartSupplier.supplier.id)
            }
        }
    }

    private fun shoppingCartSupplier(processorService: ProcessorService, product: Product, shop: Shop): ShoppingCartSupplier {
        val shoppingCartSupplier = computeIfAbsent(product.supplier.id) {
            val supplierProcessor = processorService.selectProcessor(product.supplier)
            val freeTransportFrom = supplierProcessor.freeTransportFrom(product.supplier, shop)
            ShoppingCartSupplier(product.supplier, freeTransportFrom)
        }
        put(product.supplier.id, shoppingCartSupplier)
        return shoppingCartSupplier
    }
}