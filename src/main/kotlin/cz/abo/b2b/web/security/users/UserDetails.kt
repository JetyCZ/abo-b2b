package cz.abo.b2b.web.security.users

import cz.abo.b2b.web.dao.Shop
import cz.abo.b2b.web.dao.User
import org.springframework.security.crypto.password.PasswordEncoder
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size


open class UserDetails {
    fun toUser(passwordEncoder: PasswordEncoder): User {
        val user = User(
            firstname!!, lastname!!, email!!, tarif, null, passwordEncoder.encode(password), toShop()
        )
        return user
    }

    fun toShop(): Shop {
        return Shop(shopName, shopAddress, shopGps)
    }

    var firstname: @NotEmpty String? = null
    var lastname: @NotEmpty String? = null
    var email: @NotEmpty @Email String? = null
    var tarif: Tarif = Tarif.TRIAL_3_MONTHS
    var password: @Size(min = 5, max = 64, message = "Heslo musí mít 5-64 znaků") String = ""
    var shopName: String = ""
    var shopAddress: String = ""
    var shopGps: String = ""


}