package cz.abo.b2b.web.security.users

import cz.abo.b2b.web.dao.User
import cz.abo.b2b.web.dao.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class MyUserDetailsService(val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        val user: User = userRepository.findByEmail(username) ?: throw UsernameNotFoundException(username)
        return org.springframework.security.core.userdetails.User(
            username, user.passwordHash,
            listOf(SimpleGrantedAuthority(UserRole.USER.name))
        )
    }
}