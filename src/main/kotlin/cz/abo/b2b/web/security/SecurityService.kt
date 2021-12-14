package cz.abo.b2b.web.security

import com.vaadin.flow.component.UI
import com.vaadin.flow.server.VaadinServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Component


@Component
class SecurityService {
    // Anonymous or no authentication.
    fun authenticatedUser(): UserDetails? {
            val context = SecurityContextHolder.getContext()
            val principal = context.authentication.principal
            return if (principal is UserDetails) {
                context.authentication.principal as UserDetails
            } else null
            // Anonymous or no authentication.
        }

    fun logout() {
        UI.getCurrent().page.setLocation(LOGOUT_SUCCESS_URL)
        val logoutHandler = SecurityContextLogoutHandler()
        logoutHandler.logout(
            VaadinServletRequest.getCurrent().httpServletRequest, null,
            null
        )
    }

    companion object {
        private const val LOGOUT_SUCCESS_URL = "/"
    }
}