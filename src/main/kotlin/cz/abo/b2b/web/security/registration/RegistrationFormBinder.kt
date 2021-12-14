package cz.abo.b2b.web.security.registration

import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.data.binder.ValidationException
import com.vaadin.flow.data.binder.ValidationResult
import com.vaadin.flow.data.binder.Validator
import com.vaadin.flow.data.binder.ValueContext
import cz.abo.b2b.web.security.users.UserDetails

class RegistrationFormBinder(private val registrationForm: RegistrationForm) {
    /**
     * Flag for disabling first run for password validation
     */
    private var enablePasswordValidation = false

    /**
     * Method to add the data binding and validation logics
     * to the registration form
     */
    fun addBindingAndValidation() {
        val binder: BeanValidationBinder<UserDetails> = BeanValidationBinder(UserDetails::class.java)
        binder.bindInstanceFields(registrationForm)

        // A custom validator for password fields

        binder.forField(registrationForm.passwordField)
            .withValidator(this::passwordValidator).bind("password")

        // The second password field is not connected to the Binder, but we
        // want the binder to re-check the password validator when the field
        // value changes. The easiest way is just to do that manually.
        registrationForm.passwordConfirmField.addValueChangeListener { e ->
            // The user has modified the second field, now we can validate and show errors.
            // See passwordValidator() for how this flag is used.
            enablePasswordValidation = true
            binder.validate()
        }

        // Set the label where bean-level error messages go
        binder.setStatusLabel(registrationForm.errorMessageField)

        // And finally the submit button
        registrationForm.submitButton.addClickListener { event ->
            try {
                // Create empty bean to store the details into
                val userBean = UserDetails()

                // Run validators and write the values to the bean
                binder.writeBean(userBean)

                // Typically, you would here call backend to store the bean

                // Show success message if everything went well
                showSuccess(userBean)
            } catch (exception: ValidationException) {
                // validation errors are already visible for each field,
                // and bean-level errors are shown in the status label.
                // We could show additional messages here if we want, do logging, etc.
            }
        }
    }

    /**
     * Method to validate that:
     *
     *
     * 1) Password is at least 8 characters long
     *
     *
     * 2) Values in both fields match each other
     */
    private fun passwordValidator(pass1: String?, ctx: ValueContext): ValidationResult {
        /*
        * Just a simple length check. A real version should check for password
        * complexity as well!
        */
        if (pass1 == null || pass1.length < 5) {
            return ValidationResult.error("Heslo musí být alespoň 5 znaků dlouhé")
        }
        if (!enablePasswordValidation) {
            // user hasn't visited the field yet, so don't validate just yet, but next time.
            enablePasswordValidation = true
            return ValidationResult.ok()
        }
        val pass2: String = registrationForm.passwordConfirmField.value
        return if (pass1 != null && pass1 == pass2) {
            ValidationResult.ok()
        } else ValidationResult.error("Hesla se neshodují.")
    }

    /**
     * We call this method when form submission has succeeded
     */
    private fun showSuccess(userBean: UserDetails) {
        val notification = Notification.show("Váš nový účet (" + userBean.firstName +
                " ) byl úspěšně zaregistrován, vítejte.")
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS)

        // Here you'd typically redirect the user to another view
    }
}