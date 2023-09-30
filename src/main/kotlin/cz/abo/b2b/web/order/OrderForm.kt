package cz.abo.b2b.web.order

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import cz.abo.b2b.web.MainView
import cz.abo.b2b.web.dao.User
import cz.abo.b2b.web.state.order.Order
import cz.abo.b2b.web.state.shoppingcart.ShoppingCart
import org.apache.commons.lang3.StringUtils
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class OrderForm(val mainView: MainView, val order: Order, val shoppingCart: ShoppingCart) : FormLayout() {
    val from = TextField("Váš e-mail (v e-mailu jako adresa pro odpověď)")
    val to = TextField("E-mail dodavatele (kam se zašle objednávka)")
    val cc = TextField("Váš e-mail (kam se zašle kopie objednávky)")
    val subject = TextField("Předmět")
    val message = TextArea("Zpráva")
    val buttonCancel = Button("Zrušit objednávku")
    val buttonSend = Button("Odeslat objednávku")
    val buttons = HorizontalLayout()
    val orderFormBinder = Binder(OrderFormData::class.java)
    val orderFormData = OrderFormData()
    val downloadAnchor = Anchor()
    init {
        setResponsiveSteps(ResponsiveStep("25em", 1))
        buttons.add(buttonSend, buttonCancel)
        buttons.isSpacing = true

        buttonCancel.addClickListener {
            mainView.cancelOrder()
        }
        buttonSend.addClickListener {
            orderFormBinder.writeBean(orderFormData)
            mainView.sendOrder(orderFormData)
        }
        downloadAnchor.setTarget("_new")
        add(downloadAnchor)
        add(from, to, cc, subject, message, buttons)
        orderFormBinder.bindInstanceFields(this)
    }

    public fun fillFormData(
        authenticatedDbUser: User,
        orderAttachmentFileName: String
    ) {
        orderFormData.from = authenticatedDbUser.email
        val shoppingCartSupplier = shoppingCart[order.idSupplier]!!
        orderFormData.to = shoppingCartSupplier.supplier.orderEmail
        orderFormData.cc = authenticatedDbUser.email
        val shop = authenticatedDbUser.shop
        orderFormData.subject = "Objednávka - " + shop.name
        val icoLine = if (StringUtils.isEmpty(shop.ico)) "" else "IČO: ${shop.ico}\n"
        val dicLine = if (StringUtils.isEmpty(shop.dic)) "" else "DIČ: ${shop.dic}\n"
        val phoneLine = if (StringUtils.isEmpty(authenticatedDbUser.phone)) "" else "Telefon: ${authenticatedDbUser.phone}\b"
        var orderTable = ""

        for (entry in shoppingCartSupplier) {

            val shoppingCartItem = entry.value
            val product = shoppingCartItem.product
            val priceNoVAT = product.priceNoVAT
            orderTable +=
                product.ean + "\t" +
                product.productName + "\t" +
                product.unit + "\t" +
                product.quantity + "\t"
                shoppingCartItem.count.toString() + "\t"
                priceNoVAT.toPlainString() + "\t"
                priceNoVAT.multiply(product.quantity).multiply(shoppingCartItem.count.toBigDecimal()).toPlainString() + "\t"
        }
        orderFormData.message = """
Dobrý den, do našeho krámku objednávám zboží (v příloze).

Odběratel:
    ${shop.name}
    ${icoLine}${dicLine}
    
Adresa dodání:
    ${shop.name}
    ${shop.street}
    ${shop.postcode} ${shop.city}
    GPS souřadnice obchodu: ${shop.gps}

Kontaktní osoba:
    ${authenticatedDbUser.firstname} ${authenticatedDbUser.lastname}
    ${phoneLine}Email: ${authenticatedDbUser.email}

Děkuji
S pozdravem
${authenticatedDbUser.firstname} ${authenticatedDbUser.lastname}
    """
        downloadAnchor.removeAll()
        downloadAnchor.add(Span(orderAttachmentFileName))
        downloadAnchor.href = "/download-filled/" + order.idSupplier

        orderFormBinder.readBean(orderFormData)
    }
}

class OrderFormData {
    var from : @NotBlank @Email String = ""
    var to : @NotBlank @Email String = ""
    var cc : String? = null
    var subject : @NotBlank String = ""
    var message : @NotBlank String = ""
}
