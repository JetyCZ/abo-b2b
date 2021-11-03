package cz.abo.b2b.web.shoppingcart;

import java.util.HashMap;

public class ShoppingCart extends HashMap<String, Long> {


    public void add(String productId, int count) {
        Long quantity = getOrDefault(productId, 0L);
        quantity += count;
        put(productId, quantity);
    }
}
