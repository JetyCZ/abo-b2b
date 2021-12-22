package cz.abo.b2b.web.security.registration

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification.show
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.EmailField
import com.vaadin.flow.component.textfield.PasswordField
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
import cz.abo.b2b.web.security.SecurityService
import cz.abo.b2b.web.security.users.Tarif
import cz.abo.b2b.web.security.users.UserDetails
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty



class RegistrationForm(
    val passwordEncoder: PasswordEncoder,
    val userRepository: UserRepository
) : FormLayout() {

        val firstname: @NotEmpty TextField = TextField("Jméno")
        val lastname: TextField = TextField("Příjmení")
        val email: @Email EmailField = EmailField("Email")
        val password: PasswordField = PasswordField("Heslo")
        val passwordConfirm: PasswordField = PasswordField("Heslo znovu")
        val tarif : RadioButtonGroup<Tarif> = RadioButtonGroup<Tarif>()
        val errorMessageField: Span
        val submitButton: Button

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
        val title = H3("Registrace nového uživatele")
        add(
            title, firstname, lastname, email, password,
                passwordConfirm, errorMessageField, tarif,
                submitButton
            )
            maxWidth = "500px"

            setResponsiveSteps(
                ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP)
            )
            // These components always take full width
            setColspan(title, 2)
            setColspan(email, 2)
            setColspan(errorMessageField, 2)
            setColspan(submitButton, 2)
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
            binder.forField(tarif).bind("tarif")
        binder.readBean(userDetails)
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
            println("Saving user")
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