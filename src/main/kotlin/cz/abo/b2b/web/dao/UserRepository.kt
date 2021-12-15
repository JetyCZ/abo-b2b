package cz.abo.b2b.web.dao

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User?, UUID?> {
    fun findByEmail(email: String): User?
}