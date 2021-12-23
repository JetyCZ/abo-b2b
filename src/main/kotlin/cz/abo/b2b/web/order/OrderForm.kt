package cz.abo.b2b.web.order

import com.example.application.security.SecurityUtils
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import cz.abo.b2b.web.MainView
import cz.abo.b2b.web.dao.User
import cz.abo.b2b.web.state.order.Order
import cz.abo.b2b.web.state.shoppingcart.ShoppingCart
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class OrderForm(mainView: MainView, order: Order, shoppingCart: ShoppingCart) : FormLayout() {
    var authenticatedDbUser: User? = null
    val from = TextField("Váš e-mail (v e-mailu jako odesilatel)")
    val to = TextField("E-mail dodavatele (kam se zašle objednávka)")
    val cc = TextField("Váš e-mail (kam se zašle kopie objednávky)")
    val subject = TextField("Předmět")
    val message = TextArea("Zpráva")
    val buttonCancel = Button("Zrušit objednávku")
    val buttonSend = Button("Odeslat objednávku")
    val buttons = HorizontalLayout()
    val orderFormBinder = Binder<OrderFormData>()
    val orderFormData = OrderFormData()
    init {
        setResponsiveSteps(ResponsiveStep("25em", 1))
        buttons.add(buttonSend, buttonCancel)
        buttons.isSpacing = true

        buttonCancel.addClickListener {
            mainView.cancelOrder()
        }
        buttonSend.addClickListener {
            mainView.sendOrder()
        }

        add(from, to, cc, subject, message, buttons)

        SecurityUtils.isUserLoggedIn()
        orderFormBinder.bindInstanceFields(this)
        orderFormData.from = authenticatedDbUser!!.email
        orderFormData.to = shoppingCart[order.idSupplier]!!.supplier.orderEmail
        orderFormData.cc = authenticatedDbUser!!.email
        val shop = authenticatedDbUser!!.shop
        orderFormData.subject = "Objednávka - " + shop.name
        orderFormData.message = "Dobrý den, do našeho krámku objednáváme zboží.\n" +
                "Adresa dodání:\n" +
                shop.address +
                "\n" +
                "GPS souřadnice obchodu: " + shop.gps
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
