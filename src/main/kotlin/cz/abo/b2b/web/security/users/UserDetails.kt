package cz.abo.b2b.web.security.users

import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import cz.abo.b2b.web.dao.Shop
import cz.abo.b2b.web.dao.User
import cz.abo.b2b.web.security.registration.*
import org.springframework.security.crypto.password.PasswordEncoder
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size


open class UserDetails {
    fun toUser(passwordEncoder: PasswordEncoder): User {
        val user = User(
            firstname!!, lastname!!, email!!, phone!!, tarif, null, passwordEncoder.encode(password), toShop()
        )
        return user
    }

    fun toShop(): Shop {
        return Shop(shopName, shopStreet, shopCity, shopPostcode, shopIco, shopDic, shopGps)
    }

    var firstname: @NotEmpty String? = null
    var lastname: @NotEmpty String? = null
    var email: @NotEmpty @Email String? = null
    var phone: @NotEmpty String? = null
    var tarif: Tarif = Tarif.TRIAL_3_MONTHS
    var password: @Size(min = 5, max = 64, message = "Heslo musí mít 5-64 znaků") String = ""
    var shopName: String = ""
    var shopStreet: String = ""
    var shopCity: String = ""
    var shopPostcode: String = ""
    var shopIco: String = ""
    var shopDic: String = ""
    var shopGps : String = ""

}