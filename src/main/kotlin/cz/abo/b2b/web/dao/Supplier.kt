package cz.abo.b2b.web.dao

import cz.abo.b2b.web.importer.SuppliersImport
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Supplier(

    var name: String,

    /**
     * Free transport, amount without VAT
     */
    var freeTransportFrom: BigDecimal,

    @Column(columnDefinition = "LONGTEXT")
    var description: String?,
    var importUrl: String,
    var importerClassName: String

) {
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID()

    fun resourceFilePath() : String{
        return Supplier::class.java.getResource(importUrl).getFile().replace("%20", " ")
    }
}