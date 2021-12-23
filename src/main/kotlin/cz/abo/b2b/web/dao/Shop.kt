package cz.abo.b2b.web.dao

import cz.abo.b2b.web.security.users.Tarif
import java.util.*
import javax.persistence.*

@Entity
class Shop(

    var name: String,
    @Column(columnDefinition = "LONGTEXT")
    var address: String,
    var gps: String
) {

    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID()


}