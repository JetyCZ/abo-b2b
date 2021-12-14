package cz.abo.b2b.web.security.registration

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasValueAndElement
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.textfield.EmailField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import java.util.stream.Stream


class RegistrationForm : FormLayout() {

        private val title: H3
        private val firstName: TextField
        private val lastName: TextField
        private val email: EmailField
        val passwordConfirmField: PasswordField
        val errorMessageField: Span
        val passwordField: PasswordField
        val submitButton: Button

        init {
            title = H3("Signup form")
            firstName = TextField("First name")
            lastName = TextField("Last name")
            email = EmailField("Email")
            passwordField = PasswordField("Password")
            passwordConfirmField = PasswordField("Confirm password")
            setRequiredIndicatorVisible(
                firstName, lastName, email, passwordField,
                passwordConfirmField
            )
            errorMessageField = Span()
            submitButton = Button("Join the community")
            submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            add(
                title, firstName, lastName, email, passwordField,
                passwordConfirmField, errorMessageField,
                submitButton
            )

            // Max width of the Form
            maxWidth = "500px"

            // Allow the form layout to be responsive.
            // On device widths 0-490px we have one column.
            // Otherwise, we have two columns.
            setResponsiveSteps(
                ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP)
            )

            // These components always take full width
            setColspan(title, 2)
            setColspan(email, 2)
            setColspan(errorMessageField, 2)
            setColspan(submitButton, 2)
        }

        private fun setRequiredIndicatorVisible(vararg components: HasValueAndElement<*,*>) {
            for (component in components) {
                component.isRequiredIndicatorVisible = true
            }
        }
    }
}