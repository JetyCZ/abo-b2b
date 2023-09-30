package cz.abo.b2b.web.security.registration

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification.show
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.EmailField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.*
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.validator.EmailValidator
import com.vaadin.flow.data.validator.StringLengthValidator
import com.vaadin.flow.server.VaadinService
import com.vaadin.flow.server.VaadinServletRequest
import cz.abo.b2b.web.MainView
import cz.abo.b2b.web.dao.User
import cz.abo.b2b.web.dao.UserRepository
import cz.abo.b2b.web.security.users.Tarif
import cz.abo.b2b.web.security.users.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty


private const val JMENO_OBCHODU = "Jméno obchodu"
private const val ULICE_S_CISLEM_POPISNYM = "Ulice s číslem popisným"
private const val OBEC = "Obec"
private const val PSC = "PSČ"
private const val GPS_SOURADNICE_OBCHODU = "GPS souřadnice obchodu"
private const val ICO = "IČO"
private const val DIC = "DIČ"

class RegistrationForm(
    val passwordEncoder: PasswordEncoder,
    val userRepository: UserRepository,
    val shopRepository: UserRepository
) : FormLayout() {

        val firstname: @NotEmpty TextField = TextField("Jméno")
        val lastname: TextField = TextField("Příjmení")
        val email: @Email EmailField = EmailField("Email")
        val phone = TextField("Telefon")
        val password: PasswordField = PasswordField("Heslo")
        val passwordConfirm: PasswordField = PasswordField("Heslo znovu")
        val tarif : RadioButtonGroup<Tarif> = RadioButtonGroup<Tarif>()
        val errorMessageField: Span
        val submitButton: Button

        val shopName = TextField(JMENO_OBCHODU)
        val shopStreet = TextField(ULICE_S_CISLEM_POPISNYM)
        val shopCity = TextArea(OBEC)
        val shopPostcode = TextArea(PSC)
        val shopIco = TextField(ICO)
        val shopDic = TextField(DIC)
        val shopGps = TextField(GPS_SOURADNICE_OBCHODU)

        val binder: Binder<UserDetails> = Binder(UserDetails::class.java)
        val userDetails : UserDetails = UserDetails()

        /**
         * Flag for disabling first run for password validation
         */
        private var enablePasswordValidation = false

    init {
            passwordConfirm.addValueChangeListener {
                enablePasswordValidation = true
                binder.validate()
            }

            tarif.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
            tarif.label = "Tarif"
            tarif.setItems(Tarif.TRIAL_3_MONTHS, Tarif.IN_LOSS, Tarif.WITHOUT_PROFIT, Tarif.PROFITABLE)

            tarif.setRenderer(
                ComponentRenderer { tarif: Tarif ->
                    Span(tarif.message)
                })
            errorMessageField = Span()
            submitButton = Button("Vytvořit účet")
            submitButton.addClickListener { event -> validateAndSave() }
            submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)

            maxWidth = "500px"

            setResponsiveSteps(
                ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP)
            )
            val h2 = H2("Registrace nového uživatele")
            setColspan(h2, 2)
            add(h2)
            addUserInputs()
            addShopInputs()
            add(submitButton)
            setColspan(submitButton, 2)
            binder.readBean(userDetails)
        }

    private fun addUserInputs() {
        val h3 = H3("Informace o uživateli")
        add(h3)
        setColspan(h3, 2)
        add(
            firstname, lastname, email, phone, password,
            passwordConfirm, errorMessageField, tarif
        )
        // These components always take full width
        setColspan(email, 2)
        setColspan(errorMessageField, 2)
        setColspan(tarif, 2)

        binder.forField(firstname)
            .withValidator(StringLengthValidator("Jméno nesmí být prázdné a musí mít alespoň 3 znaky", 3, null))
            .bind("firstname")
        binder.forField(lastname)
            .withValidator(StringLengthValidator("Jméno nesmí být prázdné a musí mít alespoň 3 znaky", 3, null))
            .bind("lastname")
        binder.forField(password)
            .withValidator(this::passwordValidator)
            .bind("password")
        binder.forField(email)
            .withValidator(EmailValidator("Prosím zadejte platnou e-mailovou adresu"))
            .bind("email")
        binder.forField(phone)
            .withValidator(StringLengthValidator("Telefon nesmí být prázdný a musí mít alespoň 9 znaků", 9, null))
            .bind("phone")
        binder.forField(tarif).bind("tarif")
    }

    private fun addShopInputs() {
        val h3 = H3("Informace o obchodu")
        add(h3)
        setColspan(h3, 2)
        add(shopName, shopIco, shopDic)
        binder.forField(shopIco)
            .bind("shopIco")
        binder.forField(shopDic)
            .bind("shopDic")
        val h4 = H4("Adresa obchodu")
        add(h4)
        setColspan(h4, 2)
        shopGps.placeholder = "50.0648194N, 15.7965928E"
        add(shopStreet, shopPostcode, shopCity, shopGps)
        binder.forField(shopName)
            .withValidator(StringLengthValidator("$JMENO_OBCHODU nesmí být prázdné a musí mít alespoň 3 znaky", 3, null))
            .bind("shopName")
        binder.forField(shopStreet)
            .withValidator(StringLengthValidator("$ULICE_S_CISLEM_POPISNYM nesmí být prázdná a musí mít alespoň 6 znaků", 6, null))
            .bind("shopStreet")
        binder.forField(shopCity)
            .withValidator(StringLengthValidator("$OBEC nesmí být prázdná a musí mít alespoň 2 znaky", 2, null))
            .bind("shopCity")
        binder.forField(shopPostcode)
            .withValidator(StringLengthValidator("$PSC nesmí být prázdné a musí mít právě 5 znaků", 5, 5))
            .bind("shopPostcode")

        binder.forField(shopGps)
            .withValidator(StringLengthValidator("GPS obchodu nesmí být prázdné a musí mít právě 24 znaků", 24, 24))
            .bind("shopGps")
        binder.forField(tarif).bind("tarif")
    }

    /**
     * Method to validate that:
     *
     *
     * 1) Password is at least 8 characters long
     *
     *
     * 2) Values in both fields match each other
     */
    private fun passwordValidator(pass1: String?, ctx: ValueContext): ValidationResult {
        /*
        * Just a simple length check. A real version should check for password
        * complexity as well!
        */
        if (pass1 == null || pass1.length < 5) {
            return ValidationResult.error("Heslo musí být alespoň 5 znaků dlouhé")
        }
        if (!enablePasswordValidation) {
            // user hasn't visited the field yet, so don't validate just yet, but next time.
            enablePasswordValidation = true
            return ValidationResult.ok()
        }
        val pass2: String = passwordConfirm.value
        return if (pass1 != null && pass1 == pass2) {
            ValidationResult.ok()
        } else ValidationResult.error("Hesla se neshodují.")
    }



    private fun validateAndSave() {
        try {
            binder.writeBean(userDetails)
            println("Saving user and shop")
            var user: User = userDetails.toUser(passwordEncoder)
            userRepository.save(user)


            show("Váš uživatelský účet ${user.email} byl v pořádku vytvořen, automaticky vás přihlásíme.")
            autoLoginUser(userDetails)
            UI.getCurrent().navigate(MainView::class.java)
        } catch (e: ValidationException) {
            e.printStackTrace()
        }
    }

    fun autoLoginUser(userDetails: UserDetails) {
        val currentRequest = VaadinService.getCurrentRequest()
        val vaadinServletRequest : VaadinServletRequest = currentRequest as VaadinServletRequest
        vaadinServletRequest.httpServletRequest.login(userDetails.email, userDetails.password)
    }
}
/*
abstract class ContactFormEvent(val source: RegistrationForm?, val userDetails: UserDetails?) :
    ComponentEvent<RegistrationForm?>(source, false) {
    fun getUserDetails(): UserDetails? {
        return userDetails
    }
}

class SaveEvent internal constructor(source: ContactForm?, contact: Contact?) : ContactFormEvent(source, contact)

class DeleteEvent internal constructor(source: ContactForm?, contact: Contact?) :
    ContactFormEvent(source, contact)

class CloseEvent internal constructor(source: ContactForm?) : ContactFormEvent(source, null)

fun <T : ComponentEvent<*>?> addListener(
    eventType: Class<T>?,
    listener: ComponentEventListener<T>?
): Registration? {
    return getEventBus().addListener(eventType, listener)
}
*/
