package cz.abo.b2b.web.security.view
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


abstract class AbstractLoginView : VerticalLayout() {

    init {
        initUserFormCommon()
    }

    protected fun initUserFormCommon() {
        setSizeFull()
        justifyContentMode = JustifyContentMode.CENTER
        alignItems = FlexComponent.Alignment.CENTER
        add(H1("Asociace Bezobalu - B2B"))
    }

}