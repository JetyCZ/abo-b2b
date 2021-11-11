package cz.abo.b2b.web

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
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
import com.vaadin.flow.data.provider.SortDirection
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import cz.abo.b2b.web.component.StyledText
import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.ProductRepository
import cz.abo.b2b.web.shoppingcart.ShoppingCart
import cz.abo.b2b.web.shoppingcart.ShoppingCartItem
import cz.abo.b2b.web.shoppingcart.ShoppingCartSupplier
import org.apache.commons.lang3.StringUtils
import org.vaadin.klaudeta.PaginatedGrid
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList


@Route
class MainView(val productRepository: ProductRepository,
                val shoppingCart: ShoppingCart) : VerticalLayout() {

    private val productGrid: PaginatedGrid<Product> = PaginatedGrid(Product::class.java)

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
        val rightColumn = VerticalLayout()

        val filter = TextField()
        filter.placeholder = "Filtrovat podle názvu zboží";
        filter.valueChangeMode = ValueChangeMode.EAGER
        filter.addValueChangeListener { e -> listProducts(e.value) }
        rightColumn.add(filter)

        buildProductGrid()
        displayShoppingCart()

        rightColumn.add(productGrid)

        workspace.add(rightColumn)
        workspace.element.style.set("background-color","#FFFFA0")
        add(workspace)


}

    private fun buildProductGrid() {

        val productList = productRepository.findAll()
        productGrid.setItems(productList)
        productGrid.removeAllColumns()
        productGrid.addColumn("supplier.name").setHeader("Dodavatel")
        val productColumn = productGrid.addColumn(Product::productName).setHeader("Název zboží").setWidth("70%")
        productGrid.addColumn(Product::priceNoVAT).setHeader(
            Html("<span>Cena<br>bez DPH<br>za m.j.</span>"))
        productGrid.addColumn(Product::quantity).setHeader(
            Html("<span>Množství<br>(ks/kg/l)</span>"))
        productGrid.addComponentColumn(this::buildBuyButton).setHeader("Akce")
        productGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        productGrid.setPaginatorTexts("Strana", "z")
        productGrid.sort(Collections.singletonList(GridSortOrder(productColumn, SortDirection.ASCENDING)))
        productGrid.setItemDetailsRenderer(
            ComponentRenderer { product: Product ->
                val layout = VerticalLayout()
                layout.width = "100%"
                val description: String
                if (product.description == null) {
                    description = "<i>Žádný popis</i>"
                } else {
                    description = "<b>Popis:</b> " + product.description
                }
                layout.add(
                    StyledText(description)
                )
                layout
            })
        productGrid.height = "70vh"
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
        displayShoppingCart()
    }

    private fun displayShoppingCart() {

        leftColumn.removeAll()
        for (shoppingCartEntry in shoppingCart.entries) {
            oneSupplierShoppingCart(shoppingCartEntry)
        }


    }

    private fun oneSupplierShoppingCart(shoppingCartEntry: MutableMap.MutableEntry<UUID, ShoppingCartSupplier>) {
        val shoppingCartGrid: Grid<ShoppingCartItem> = Grid(ShoppingCartItem::class.java)

        shoppingCartGrid.removeAllColumns()
        shoppingCartGrid.addColumn("product.productName").setHeader("V košíku")
        shoppingCartGrid.addColumn("count").setHeader("Počet")
        var items = ArrayList<ShoppingCartItem>()
        val shoppingCartItem = shoppingCartEntry.value
        items.addAll(shoppingCartItem.values)
        shoppingCartGrid.setItems(items)

        val oneSupplierDiv = VerticalLayout()
        oneSupplierDiv.element.style.set("background-color", "#F0EEF0")
        oneSupplierDiv.add(Label("Košík - " + shoppingCartItem.supplier.name))
        oneSupplierDiv.add(shoppingCartGrid)
        oneSupplierDiv.add(
            Html(
                "<span>" +
                        "<b>Bez DPH</b>: " + shoppingCartItem.totalPriceNoVAT() +
                        "&nbsp;<b>s DPH</b>: " + shoppingCartItem.totalPriceVAT() +
                        "</span>"
            )
        )
        val remainingToFreeTransportNoVAT = shoppingCartItem.remainingToFreeTransportNoVAT()

        val transportFreeLabel =
            StringBuffer("<div>Doprava zdarma (bez DPH):" + shoppingCartItem.supplier.freeTransportFrom + " ")
        if (remainingToFreeTransportNoVAT.toDouble() > 0) {
            transportFreeLabel.append(
                "<span style='color:red'>NE</span><br><i>(chybí " +
                        remainingToFreeTransportNoVAT.setScale(0, RoundingMode.HALF_UP) + " Kč)</i>"
            )
        } else {
            transportFreeLabel.append(
                "<span style='color:green'>ANO</span><br><i>(víc o " +
                        remainingToFreeTransportNoVAT.multiply(BigDecimal(-1))
                            .setScale(0, RoundingMode.HALF_UP) + " Kč)</i>"
            )
        }
        transportFreeLabel.append("</div>")
        oneSupplierDiv.add(Html(transportFreeLabel.toString()))
        val downloadAnchor = "<a href='/download-filled/" +shoppingCartEntry.key + "' target='_new'>Stáhnout vyplněný ceník</a>"
        oneSupplierDiv.add(Html(downloadAnchor))
        leftColumn.add(oneSupplierDiv)
    }

    private fun addHeader() {
        val drawer: Icon = VaadinIcon.MENU.create()
        val title = Span("Asociace Bezobalu - B2B platforma")
        val help: Icon = VaadinIcon.QUESTION_CIRCLE.create()

        val actionButton1 = Tab(VaadinIcon.HOME.create(), Span("Domů"))
        val actionButton2 = Tab(VaadinIcon.USERS.create(), Span("Nastavení"))
        val actionButton3 = Tab(VaadinIcon.PACKAGE.create(), Span("Odhlásit"))
        val buttonBar = Tabs(actionButton1, actionButton2, actionButton3)
        val topMenu = HorizontalLayout(buttonBar)
        topMenu.justifyContentMode = JustifyContentMode.CENTER
        topMenu.width = "100%"

        val header = HorizontalLayout(drawer, title, topMenu, help)
        header.expand(title)
        header.isPadding = true
        header.width = "100%"
        add(header)
    }

    private fun listProducts(productName: String?) {
        if (StringUtils.isEmpty(productName)) {
            productGrid.setItems(productRepository.findAll())
        } else {
            productGrid.setItems(productRepository.findByProductNameContainingIgnoreCase(productName))
        }
    }
}


