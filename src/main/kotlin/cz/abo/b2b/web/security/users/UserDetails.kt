package cz.abo.b2b.web.security.users

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


class UserDetails {
    var firstName: @NotBlank String? = null
    var lastName: @NotBlank String? = null
    var email: @NotBlank @Email String? = null
    var isAllowsMarketing = false

    // FIXME Passwords should never be stored in plain text!
    var password: @Size(min = 8, max = 64, message = "Password must be 8-64 char long") String? = null
}