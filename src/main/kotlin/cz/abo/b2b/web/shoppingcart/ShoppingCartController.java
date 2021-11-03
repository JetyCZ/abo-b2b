package cz.abo.b2b.web.shoppingcart;

import cz.abo.b2b.web.dao.ProductRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController("/cart")
public class ShoppingCartController {

    private static final String CART = "CART";
    @Autowired
    ProductRepository productRepository;

    // TODO change to POST
    @GetMapping("/add")
    public String cartAdd(@RequestParam String productId, HttpSession session) {
        ShoppingCart cart = lazyGetCart(session);
        cart.add(productId, 1);

        return "index";
    }

    @NotNull
    private ShoppingCart lazyGetCart(HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute(CART);
        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute(CART, cart);
        }
        return cart;
    }

    @GetMapping("/get")
    public ShoppingCart cartGet(HttpSession session) {
        ShoppingCart cart = lazyGetCart(session);
        return cart;
    }

    @PostMapping("/destroy")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/";
    }

}
