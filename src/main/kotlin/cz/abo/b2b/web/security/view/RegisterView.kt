package cz.abo.b2b.web.security.view

import com.vaadin.flow.router.Route
import cz.abo.b2b.web.dao.UserRepository
import cz.abo.b2b.web.security.registration.RegistrationForm
import cz.abo.b2b.web.security.registration.RegistrationFormBinder
import org.springframework.security.crypto.password.PasswordEncoder


@Route("/register")
class RegistrationView(val userRepository: UserRepository, passwordEncoder: PasswordEncoder) : AbstractLoginView() {
    init {
        val registrationForm = RegistrationForm()
        // Center the RegistrationForm
        add(registrationForm)
        val registrationFormBinder = RegistrationFormBinder(registrationForm, userRepository, passwordEncoder)
        registrationFormBinder.addBindingAndValidation()
    }
}