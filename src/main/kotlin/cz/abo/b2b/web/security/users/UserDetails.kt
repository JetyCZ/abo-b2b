package cz.abo.b2b.web.security.users

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


open class UserDetails {
    var firstName: @NotBlank String? = null
    var sureName: @NotBlank String? = null
    var email: @NotBlank @Email String? = null
    var tarif: Tarif = Tarif.TRIAL_3_MONTHS
    // FIXME Passwords should never be stored in plain text!
    var password: @Size(min = 5, max = 64, message = "Heslo musí mít 5-64 znaků") String? = null
}