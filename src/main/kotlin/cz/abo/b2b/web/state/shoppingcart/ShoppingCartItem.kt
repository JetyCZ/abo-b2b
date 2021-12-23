package cz.abo.b2b.web.state.shoppingcart

import cz.abo.b2b.web.dao.Product

class ShoppingCartItem (val product: Product, var count: Long) {
}