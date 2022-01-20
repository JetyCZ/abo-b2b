package cz.abo.b2b.web.security

import com.vaadin.flow.component.UI
import com.vaadin.flow.server.VaadinService
import com.vaadin.flow.server.VaadinServletRequest
import cz.abo.b2b.web.dao.Shop
import cz.abo.b2b.web.dao.User
import cz.abo.b2b.web.dao.UserRepository
import cz.abo.b2b.web.security.users.Tarif
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Component


@Component
class SecurityService(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder) {

    init {
        val testEmail = "pavel.jetensky@seznam.cz"
        var testUser = userRepository.findByEmail(testEmail)
        if (testUser==null) {
            testUser = User("Pavel", "Jetenský", testEmail, "777045366", Tarif.PROFITABLE, "",passwordEncoder.encode("test"),
            Shop("Krámek bezobalu v Brozanech u Pardubic", "Brozany 7", "53352", "Staré Hradiště", "04641515", null, "50.0648194N, 15.7965928E")
            )
            userRepository.save(testUser)
        }
    }

    // Anonymous or no authentication.
    fun authenticatedUser(): UserDetails? {
            val context = SecurityContextHolder.getContext()
            val principal = context.authentication.principal
            return if (principal is UserDetails) {
                context.authentication.principal as UserDetails
            } else null
            // Anonymous or no authentication.
        }

    // Anonymous or no authentication.
    fun authenticatedDbUser(): User? {
            val context = SecurityContextHolder.getContext()
            val principal = context.authentication.principal
            if (principal is UserDetails) {
                val email = (context.authentication.principal as UserDetails).username
                return userRepository.findByEmail(email)
            } else {
                return null
            }
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