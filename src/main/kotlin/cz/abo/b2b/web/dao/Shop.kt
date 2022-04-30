package cz.abo.b2b.web.dao

import java.util.*
import javax.persistence.*

@Entity
class Shop(

    var name: String,
    var street: String,
    var postcode: String,
    var city: String,
    var ico: String?,
    var dic: String?,
    var gps: String,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L


}