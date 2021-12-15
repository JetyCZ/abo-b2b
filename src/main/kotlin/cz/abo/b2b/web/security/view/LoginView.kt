package cz.abo.b2b.web.security.view
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.login.LoginI18n
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route


@Route("login")
@PageTitle("Asociace bezobalu B2B - Přihlášení")
class LoginView : AbstractLoginView(), BeforeEnterObserver {
    private val loginForm = LoginForm()

    init {
        addClassName("login-view")
        loginForm.action = "login"
        add(loginForm)
        add(Anchor("/register","Zaregistrovat nový účet"))
        loginForm.setI18n(createLoginI18n())
    }

    private fun createLoginI18n(): LoginI18n {
        val i18n = LoginI18n.createDefault()
            i18n.header = LoginI18n.Header()
            i18n.form = LoginI18n.Form()
            i18n.header.title = "Asociace bezobalu - B2B"
            i18n.header.description = "Aplikace pro snadné objednávání určená pro bezobalové obchody"
            i18n.form.username = "Uživatelské jméno"
            i18n.form.title = "Přihlášení"
            i18n.form.submit = "Přihlásit"
            i18n.form.password = "Heslo"
            i18n.form.forgotPassword = "Zapomněl jsem heslo"
            i18n.errorMessage.title = "Prosím zkontrolujte uživatele a heslo, vámi zadaná kombinace není správná."
            i18n.errorMessage.message = "Zkontrolujte své uživatelské jméno a heslo a zkuste to znovu."
            i18n.additionalInformation = ""
            return i18n
    }

    override fun beforeEnter(beforeEnterEvent: BeforeEnterEvent) {
        if (beforeEnterEvent.location
                .queryParameters
                .parameters
                .containsKey("error")
        ) {
            loginForm.isError = true
        }
    }
}