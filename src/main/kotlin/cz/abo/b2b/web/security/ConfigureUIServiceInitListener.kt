package cz.abo.b2b.web.security

import com.example.application.security.SecurityUtils
import com.vaadin.flow.component.UI
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener
import cz.abo.b2b.web.security.view.LoginView
import org.springframework.stereotype.Component

@Component
class ConfigureUIServiceInitListener : VaadinServiceInitListener {

    override fun serviceInit(event: ServiceInitEvent) {
        event.getSource().addUIInitListener { uiEvent ->
            val ui: UI = uiEvent.getUI()
            ui.addBeforeEnterListener { event: BeforeEnterEvent -> beforeEnter(event) }
        }
    }

    /**
     * Reroutes the user if (s)he is not authorized to access the view.
     *
     * @param event
     * before navigation event with event details
     */
    private fun beforeEnter(event: BeforeEnterEvent) {
        if (!LoginView::class.java.equals(event.getNavigationTarget()) // (3)
            && !SecurityUtils.isUserLoggedIn()
        ) {
            event.rerouteTo(LoginView::class.java) // (5)
        }
    }
}