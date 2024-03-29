package cz.abo.b2b.web.dao

import cz.abo.b2b.web.security.users.Tarif
import java.util.*
import javax.persistence.*

@Entity
class User(

    var firstname: String,
    var lastname: String,
    @Column(unique = true)
    var email: String,
    var phone: String,
    var tarif: Tarif,

    @Column(columnDefinition = "LONGTEXT")
    var orderingSignature: String?,
    var passwordHash: String,

    @OneToOne(cascade = [CascadeType.ALL])
    var shop: Shop

) {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

}