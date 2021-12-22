package cz.abo.b2b.web.dao

import cz.abo.b2b.web.security.users.Tarif
import java.util.*
import javax.persistence.*

@Entity
class User(

    var firstname: String,
    var lastname: String,
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