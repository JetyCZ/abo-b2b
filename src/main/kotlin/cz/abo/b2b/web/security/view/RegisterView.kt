package cz.abo.b2b.web.security.view
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import cz.abo.b2b.web.dao.UserRepository
import cz.abo.b2b.web.security.SecurityService
import cz.abo.b2b.web.security.registration.RegistrationForm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder


@Route("register")
@PageTitle("Asociace bezobalu B2B - Přihlášení")
@AnonymousAllowed
open class RegisterView(userRepository: UserRepository, passwordEncoder: PasswordEncoder) : VerticalLayout() {
    private val registrationForm = RegistrationForm(passwordEncoder, userRepository)
    init {
        //addClassName("login-view")
        // Center the RegistrationForm
        setSizeFull()
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, registrationForm)
        add(registrationForm)
    }

}