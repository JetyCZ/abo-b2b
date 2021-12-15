package cz.abo.b2b.web.security.users

import cz.abo.b2b.web.dao.User
import org.springframework.security.core.userdetails.UserDetails

class MyUserPrincipal(user: User) : UserDetails {
    private val user: User

    init {
        this.user = user
    } //...
}