package cz.abo.b2b.web

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.ItemLabelGenerator
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.SortDirection
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.renderer.LocalDateRenderer
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import cz.abo.b2b.web.dao.Product
import cz.abo.b2b.web.dao.ProductRepository
import cz.abo.b2b.web.dao.Supplier
import cz.abo.b2b.web.dao.SupplierRepository
import cz.abo.b2b.web.importer.xls.service.ProcessorService
import cz.abo.b2b.web.order.EmailService
import cz.abo.b2b.web.order.OrderForm
import cz.abo.b2b.web.order.OrderFormData
import cz.abo.b2b.web.security.SecurityService
import cz.abo.b2b.web.state.order.Order
import cz.abo.b2b.web.state.shoppingcart.ShoppingCart
import cz.abo.b2b.web.state.shoppingcart.ShoppingCartItem
import cz.abo.b2b.web.state.shoppingcart.ShoppingCartSupplier
import cz.abo.b2b.web.view.component.StyledText
import cz.abo.b2b.web.view.component.ViewUtils.Companion.round
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.StringEscapeUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.annotation.Secured
import org.vaadin.gatanaso.MultiselectComboBox
import org.vaadin.klaudeta.PaginatedGrid
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors
import javax.annotation.security.PermitAll


@Route
@PermitAll
@PageTitle("Asociace Bezobalu - B2B")
@Secured("USER")
class MainView(val productRepository: ProductRepository,
               val supplierRepository: SupplierRepository,
               val shoppingCart: ShoppingCart,
               val securityService: SecurityService,
               val emailService: EmailService,
               val order: Order,
               val processorService: ProcessorService
) : VerticalLayout() {

    companion object {
        public val dateFormatter = DateTimeFormatter.ofPattern("dd.MM. yyyy") // 2024-02-24
    }

    @Value("\${vaadin.servlet.productionMode}")
    val productionMode: String? = null

    private val productGrid: PaginatedGrid<Product> = PaginatedGrid(Product::class.java)

    private val leftColumn  = VerticalLayout()
    private val productsColumn = VerticalLayout()
    private val orderColumn = VerticalLayout()
    private val orderForm = OrderForm(this, order, shoppingCart)
    private val productNameFilter = TextField()
    private val supplierFilterCombo = MultiselectComboBox<Supplier>()

    init {
        height = "100%"
        element.style.set("background-color","#FCFFFC")

        addHeader()

        // WORKSPACE
        val workspace = HorizontalLayout()
        workspace.setSizeFull()

        leftColumn.width = "33%"
        leftColumn.height = null
        workspace.add(leftColumn)

        val filters = HorizontalLayout()

        productNameFilter.placeholder = "Filtruj dle názvu";
        productNameFilter.valueChangeMode = ValueChangeMode.EAGER
        productNameFilter.addValueChangeListener { e -> listProducts() }
        filters.add(productNameFilter)

        var itemLabelGenerator : ItemLabelGenerator<Supplier> = ItemLabelGenerator { s -> s.name }
        supplierFilterCombo.placeholder = "Filtruj dle dodavatele"
        supplierFilterCombo.setItems(supplierRepository.findAll())
        supplierFilterCombo.setItemLabelGenerator(itemLabelGenerator)
        supplierFilterCombo.addValueChangeListener { event -> listProducts()}
        filters.add(supplierFilterCombo)

        productsColumn.add(filters)
        buildProductGrid()

        refreshProductGrid()
        productsColumn.add(productGrid)
        orderColumn.isVisible = false
        productsColumn.add(orderColumn)

        refreshShoppingCart()

        workspace.add(productsColumn)
        workspace.add(orderColumn)
        workspace.element.style.set("background-color","#FFFFA0")
        add(workspace)

}


    private fun buildProductGrid() {
        productGrid.removeAllColumns()
        productGrid.addColumn("supplier.name").setHeader("Dodavatel")
        val productColumn = productGrid.addColumn(Product::productName).setHeader("Název zboží").setWidth("60%")
        productGrid.addColumn(LocalDateRenderer(Product::bestBefore, "dd.MM. yyyy"))
            .setHeader("DMT")
        productGrid.addColumn(Product::priceNoVAT).setHeader(
            Html("<span>Cena<br>bez DPH<br>za m.j.</span>")
        )
        productGrid.addColumn(Product::quantity).setHeader(
            Html("<span>Množství<br>(ks/kg/l)</span>")
        )
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

    private fun refreshOrderForm() {


        val orderTabActive = order.idSupplier != null && shoppingCart.containsKey(order.idSupplier)
        if (orderTabActive) {
            val supplier = shoppingCart[order.idSupplier]!!.supplier
            val orderProcessor = processorService.selectProcessor(supplier)
            val orderAttachmentFileName = orderProcessor.orderAttachmentFileName(supplier)

            val authenticatedDbUser = securityService.authenticatedDbUser()
            orderForm.fillFormData(authenticatedDbUser!!, orderAttachmentFileName!!)

            orderColumn.removeAll()
            orderColumn.add(H1("Objednat zboží od " + supplier.name))
            orderColumn.add(orderForm)
        }
        orderColumn.isVisible = orderTabActive
        productsColumn.isVisible = !orderTabActive

    }

    private fun refreshProductGrid() {
        val productList = productRepository.findAll()
        productGrid.setItems(productList)
    }

    /*private fun onItemClick(event: ItemClickEvent<Product>) {

    }*/

    private fun buildCartQuantity(shoppingCartItem: ShoppingCartItem): IntegerField? {
        val countField = IntegerField()
        countField.min = 0
        countField.value = shoppingCartItem.count.toInt()
        countField.setHasControls(true)


        countField.addValueChangeListener {
                event ->
            updateCartItem(shoppingCartItem.product, event.value)
        }
        return countField
    }


    private fun buildBuyButton(p: Product): Button? {
        val button = Button("Koupit")
        button.addClickListener {
            addToCart(p)
        }
        return button
    }

    private fun addToCart(product: Product) {
        Notification.show("Produkt '" + product.productName + "' byl přidán do košíku", 2000, Notification.Position.TOP_CENTER)
        shoppingCart.add(processorService, product, 1, securityService.authenticatedDbUser()!!.shop)
        refreshShoppingCart()
    }

    private fun updateCartItem(product: Product, value: Int?) {
        if (value!=null) {
            shoppingCart.update(processorService, product, value.toDouble(), securityService.authenticatedDbUser()!!.shop)
            refreshShoppingCart()
        }
    }


    private fun refreshShoppingCart() {
        leftColumn.removeAll()
        for (shoppingCartEntry in shoppingCart.entries) {
            oneSupplierShoppingCart(shoppingCartEntry)
        }
    }

    private fun oneSupplierShoppingCart(shoppingCartEntry: MutableMap.MutableEntry<Long, ShoppingCartSupplier>) {
        val shoppingCartGrid: Grid<ShoppingCartItem> = Grid(ShoppingCartItem::class.java)
        shoppingCartGrid.setSizeUndefined()
        shoppingCartGrid.removeAllColumns()


        val productColumn = shoppingCartGrid.addComponentColumn { item ->

            val product = item.product
            val orderedUnits = product.quantity.multiply(BigDecimal(item.count));
            val ean = if (!StringUtils.isEmpty(product.ean)) "\nEAN: "  + product.ean else ""
            val supplierCode = if (!StringUtils.isEmpty(product.supplierCode)) "\nKód dodavatele: "  + product.supplierCode else ""
            val bestBefore = if (product.bestBefore!=null) "\nDatum minimální trvanlivost: "  + MainView.dateFormatter.format(product.bestBefore) else ""
            val rowNum = if (product.rowNum>0) "\nČíslo řádku v Excel ceníku: "  + (product.rowNum+1) else ""
            val description = if (!StringUtils.isEmpty(product.description)) "\nPopis: "  + product.description else ""
            val productNameHtml = StringEscapeUtils.escapeHtml4(product.productName).replace("'", "&#39;")
            val title = """
Název produktu: ${productNameHtml}
Cena za ${product.unit.name.lowercase()} bez DPH: ${round(product.priceNoVAT(1))}
Cena za ${product.unit.name.lowercase()} s DPH: ${round(product.priceVAT())}
Objednáno balení: ${item.count}
Velikost balení: ${product.quantity} ${product.unit.name.lowercase()}
Celkem objednáno: ${orderedUnits} ${product.unit.name.lowercase()}
Celkem cena bez DPH: ${round(item.totalPriceNoVAT())}
Celkem cena s DPH: ${round(item.totalPriceVAT())}${ean}${supplierCode}${bestBefore}${rowNum}${description}                
            """.trimIndent()

            Html("<span title='${title}'>${productNameHtml}</span>")

        }
        productColumn.setHeader("V košíku")

        shoppingCartGrid.addComponentColumn(this::buildCartQuantity).setHeader("Množství")
        var items = ArrayList<ShoppingCartItem>()
        val shoppingCartItem = shoppingCartEntry.value
        items.addAll(shoppingCartItem.values)
        shoppingCartGrid.setItems(items)

        val oneSupplierDiv = VerticalLayout()
        oneSupplierDiv.element.style.set("background-color", "#F0EEF0")
        val idSupplier = shoppingCartEntry.key
        oneSupplierDiv.add(Label("Košík - " + shoppingCartItem.supplier.name))
        oneSupplierDiv.add(shoppingCartGrid)
        oneSupplierDiv.add(
            Html(
                "<span>" +
                        "<b>Bez DPH</b>: " + round(shoppingCartItem.totalPriceNoVAT()) +
                        "&nbsp;<b>s DPH</b>: " + round(shoppingCartItem.totalPriceVAT()) +
                        "</span>"
            )
        )
        val remainingToFreeTransportNoVAT = shoppingCartItem.remainingToFreeTransportNoVAT()

        val transportFreeLabel =
            StringBuffer("<div>Doprava zdarma (bez DPH):" + round(shoppingCartItem.supplier.freeTransportFrom) + " ")
        if (remainingToFreeTransportNoVAT.toDouble() > 0) {
            transportFreeLabel.append(
                "<span style='color:red'>NE</span><br><i>(chybí " +
                        round(remainingToFreeTransportNoVAT) + " Kč)</i>"
            )
        } else {
            transportFreeLabel.append(
                "<span style='color:green'>ANO</span><br><i>(víc o " +
                        round(remainingToFreeTransportNoVAT.multiply(BigDecimal(-1)))
                             + " Kč)</i>"
            )
        }
        transportFreeLabel.append("</div>")
        oneSupplierDiv.add(Html(transportFreeLabel.toString()))
        val orderButton = Button("Objednat")
        orderButton.addClickListener {
            order.idSupplier = idSupplier
            refreshOrderForm()
        }
        oneSupplierDiv.add(orderButton)
        leftColumn.add(oneSupplierDiv)
    }

    private fun addHeader() {
        val drawer: Icon = VaadinIcon.MENU.create()
        val title = Span("Asociace Bezobalu - B2B platforma")
        val help: Icon = VaadinIcon.QUESTION_CIRCLE.create()

        val actionButton1 = Tab(VaadinIcon.HOME.create(), Span("Produkty"))
        val actionButton2 = Tab(VaadinIcon.USERS.create(), Span("Objednávka"))
        val tabs = Tabs()
        tabs.add(actionButton1)
        if (order.idSupplier!=null) {
            tabs.add(actionButton2)
        }

        val topMenu: HorizontalLayout

        if (securityService.authenticatedUser() != null) {
            val logoutButton = Button(
                "Odhlásit"
            ) { click: ClickEvent<Button?>? -> securityService.logout() }
            topMenu = HorizontalLayout(tabs, logoutButton)
        } else {
            topMenu = HorizontalLayout(tabs)
        }


        topMenu.justifyContentMode = JustifyContentMode.CENTER
        topMenu.width = "100%"

        val header = HorizontalLayout(drawer, title, topMenu, help)
        header.expand(title)
        header.isPadding = true
        header.width = "100%"
        add(header)
    }

    private fun listProducts() {
        val productName = productNameFilter.value
        val selectedSuppliers = supplierFilterCombo.selectedItems
        val supplierIds = selectedSuppliers.stream().map { s -> s.id }.collect(Collectors.toList())
        if (StringUtils.isEmpty(productName) && supplierIds.isEmpty()) {
            productGrid.setItems(productRepository.findAll())
        } else {
            val items: List<Product>
            if (supplierIds.isEmpty()) {
                items = productRepository.find(productName)
            } else {
                items = productRepository.find(productName, supplierIds)
            }
            productGrid.setItems(items)
        }
    }

    fun cancelOrder() {
        order.idSupplier = null
        refreshOrderForm()
    }

    fun sendOrder(orderFormData: OrderFormData) {
        val fileAttachment = processorService.getFilledPriceListWithOrder(order.idSupplier)
        val result = emailService.sendMailWithAttachment(
            orderFormData.from,
            orderFormData.to,
            orderFormData.cc,
            orderFormData.subject,
            orderFormData.message,
            fileAttachment
        )
        var message: String
        if (result) {
            message = "Vaše objednávka byla odeslána na ${orderFormData.to}."
            if (!StringUtils.isEmpty(orderFormData.cc)) {
                message += " Kopie objednávky byla zaslána na váš e-mail ${orderFormData.cc}"
            }
            shoppingCart.remove(order.idSupplier)
            order.idSupplier = null
            refreshOrderForm()
            refreshShoppingCart()
            Notification.show(message)
        } else {
            message = "Při odesílání vaší objednávky e-mailem došlo bohužel k chybě."
            val notification: Notification = Notification(message, 10000)
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open()
        }

    }
}




