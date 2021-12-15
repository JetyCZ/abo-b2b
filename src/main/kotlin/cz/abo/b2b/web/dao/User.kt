package cz.abo.b2b.web.dao

import cz.abo.b2b.web.security.users.Tarif
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.persistence.*

@Entity
class User(

    var firstname: String,

    var surname: String,
    var email: String,
    var tarif: Tarif,

    @Column(columnDefinition = "LONGTEXT")
    var orderingSignature: String?,

    var passwordHash: String,


    ) {

    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID()


}