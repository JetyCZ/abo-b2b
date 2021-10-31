package cz.abo.b2b.web

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import cz.abo.b2b.web.dao.ProductRepository


@Route
class MainView(val productRepository: ProductRepository) : VerticalLayout() {

    init {


        val grid: Grid<Product> = Grid(Product::class.java)

        val productList = productRepository.findAll()

        grid.setItems(productList)

        add(grid)

        add(Button(
            "Click me"
        ) { e: ClickEvent<Button?>? ->
            Notification.show(
                "Hello, Spring+Vaadin user!"
            )
        })
}
}