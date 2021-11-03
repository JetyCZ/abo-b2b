package cz.abo.b2b.web

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import cz.abo.b2b.web.component.StyledText
import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.ProductRepository
import cz.abo.b2b.web.shoppingcart.ShoppingCart
import cz.abo.b2b.web.shoppingcart.ShoppingCartItem
import org.apache.commons.lang3.StringUtils


@Route
class MainView(val productRepository: ProductRepository,
                val shoppingCart: ShoppingCart) : VerticalLayout() {

    private val productGrid: Grid<Product> = Grid(Product::class.java)
    private val shoppingCartGrid: Grid<ShoppingCartItem> = Grid(ShoppingCartItem::class.java)
    private val leftColumn : VerticalLayout = VerticalLayout()
    init {
        height = "100%"
        element.style.set("background-color","#FCFFFC")

        addHeader()

        // WORKSPACE
        val workspace = HorizontalLayout()
        workspace.setSizeFull()

        leftColumn.width = "33%"

        workspace.add(leftColumn)
        workspace.height = "100%"
        val rightColumn = VerticalLayout()

        val filter = TextField()
        filter.placeholder = "Filter by last name";
        filter.valueChangeMode = ValueChangeMode.EAGER
        filter.addValueChangeListener { e -> listProducts(e.value) }
        rightColumn.add(filter)

        buildProductGrid()
        shoppingCartGrid.removeAllColumns()
        shoppingCartGrid.addColumn("product.productName").setHeader("V košíku")
        shoppingCartGrid.addColumn("count").setHeader("Počet")
        leftColumn.add(shoppingCartGrid)
        displayShoppingCartContent()

        rightColumn.add(productGrid)
        workspace.add(rightColumn)
        workspace.element.style.set("background-color","#FFFFA0")
        add(workspace)
        // FOOTER

        // FOOTER
        val actionButton1 = Tab(VaadinIcon.HOME.create(), Span("Home"))
        val actionButton2 = Tab(VaadinIcon.USERS.create(), Span("Customers"))
        val actionButton3 = Tab(VaadinIcon.PACKAGE.create(), Span("Products"))
        val buttonBar = Tabs(actionButton1, actionButton2, actionButton3)
        val footer = HorizontalLayout(buttonBar)
        footer.justifyContentMode = JustifyContentMode.CENTER
        footer.width = "100%"
        footer.element.style.set("background-color","#FFA0FF")


        add(footer)
}

    private fun buildProductGrid() {
        val productList = productRepository.findAll()
        productGrid.setItems(productList)
        productGrid.removeAllColumns()
        productGrid.addColumn(Product::productName).setHeader("Název zboží")
        productGrid.addColumn(Product::priceVAT).setHeader("Cena vč. DPH")
        productGrid.addComponentColumn(this::buildBuyButton).setHeader("Akce")
        productGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        productGrid.setItemDetailsRenderer(
            ComponentRenderer { product: Product ->
                val layout = VerticalLayout()
                layout.width = "100%"
                val description: String
                if (product.description == null) {
                    description = "Žádný popis"
                } else {
                    description = "Popis: " + product.description
                }
                layout.add(
                    StyledText(description)
                )
                layout
            })
    }

    /*private fun onItemClick(event: ItemClickEvent<Product>) {

    }*/

    private fun buildBuyButton(p: Product): Button? {
        val button: Button = Button("Koupit")
        button.addClickListener { e: ClickEvent<Button?>? ->
            addToCart(p)
        }
        return button
    }

    private fun addToCart(p: Product) {
        Notification.show("Produkt '" + p.productName + "' byl přidán do košíku", 2000, Notification.Position.TOP_CENTER)
        shoppingCart.add(p, 1)
        displayShoppingCartContent()
    }

    private fun displayShoppingCartContent() {
        var items = ArrayList<ShoppingCartItem>()
        for (shoppingCartEntry in shoppingCart.entries) {
            items.add(shoppingCartEntry.value)
        }
        shoppingCartGrid.setItems(items)

    }

    private fun addHeader() {
        val drawer: Icon = VaadinIcon.MENU.create()
        val title = Span("Asociace Bezobalu - B2B platforma")
        val help: Icon = VaadinIcon.QUESTION_CIRCLE.create()
        val header = HorizontalLayout(drawer, title, help)
        header.expand(title)
        header.isPadding = true
        header.width = "100%"
        add(header)
    }

    private fun listProducts(productName: String?) {
        if (StringUtils.isEmpty(productName)) {
            productGrid.setItems(productRepository.findAll())
        } else {
            productGrid.setItems(productRepository.findByProductNameContaining(productName))
        }
    }
}


