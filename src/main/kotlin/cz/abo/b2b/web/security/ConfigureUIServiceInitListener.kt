package cz.abo.b2b.web.security

import com.example.application.security.SecurityUtils
import com.example.application.security.SecurityUtils.Companion.isUserLoggedIn
import com.vaadin.flow.component.UI
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.InternalServerError
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
        val accessGranted: Boolean = SecurityUtils.isAccessGranted(event.navigationTarget)
        if (event.navigationTarget.isAssignableFrom(InternalServerError::class.java)) {
            return
        }
        if (!accessGranted) {
            if (isUserLoggedIn()) {
                event.rerouteToError(AccessDeniedException::class.java)
            } else {
                if (!event.location.path.equals("register")) {
                    event.rerouteTo(LoginView::class.java)
                }
            }
        }
    }
}