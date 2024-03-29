package cz.abo.b2b.web.security.view
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.*
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.login.LoginI18n
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import cz.abo.b2b.web.dao.jdbc.SupplierDetails
import cz.abo.b2b.web.dao.jdbc.SupplierJdbcRepository


@Route("login")
@PageTitle("Asociace bezobalu B2B - Přihlášení")
@AnonymousAllowed
class LoginView(val supplierJdbcRepository: SupplierJdbcRepository) : AbstractLoginView(), BeforeEnterObserver {
    private val loginForm = LoginForm()
    private val mainPane = HorizontalLayout()
    private val leftSide = VerticalLayout()
    private val rightSide = VerticalLayout()

    private val supplierGrid: Grid<SupplierDetails> = Grid(SupplierDetails::class.java)
    init {
        addClassName("login-view")
        loginForm.setI18n(createLoginI18n())
        loginForm.action = "login"

        add(mainPane)
            mainPane.add(leftSide)
                leftSide.setWidthFull()
                var info = VerticalLayout()
                info.add(
                    Span("Vstoupili jste na stránku platformy, která vznikla pro účely zjednodušení spolupráce bezobalových obchodů s dodavateli kvalitních potravin a dalšího spotřebního zboží. Systém je tvořen komunitou bezobalových obchodů pro vlastní potřeby. Projekt je financován z příspěvků placených uživatelů a jeho cílem je usnadnit práci. Veškeré finance jsou zpět vraceny do rozvoje systému."),
                    Span("Pokud jste provozovatel bezobalového obchodu a máte zájem o vstup do systému, může rovnou přistoupit k registraci. Pokud jste dodavatel a chtěli byste do systému vstoupit, tak nás oslovte na " +
                    "asociacebezobalub2b[NAHRAĎTE ZA ZAVINÁČ]gmail.com"),
                    Span("Projekt byl realizován za podpory Ministerstva Životního prostředí")
                )
                leftSide.add(info)
                supplierGrid.removeAllColumns()
                supplierGrid.addColumn(SupplierDetails::name).setHeader("Zboží od těchto dodavatelů")
                supplierGrid.addColumn(SupplierDetails::productCount).setHeader("Počet produktů")
                supplierGrid.setItems(supplierJdbcRepository.supplierDetails())
                leftSide.add(supplierGrid)
            mainPane.add(rightSide)
                rightSide.add(loginForm)
                rightSide.add(Anchor("/register","Zaregistrovat nový účet"))

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