package cz.abo.b2b.web.dao

import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.ImportSourceType
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
    var importerClassName: String,
    var orderEmail: String
) {


    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID()

    fun importSource() : ImportSource{
        if (importUrl.startsWith("http")) {
            return ImportSource(
                importUrl,
                ImportSourceType.URL
            )
        } else {
            return ImportSource(
                importUrl,
                ImportSourceType.CLASSPATH_RESOURCE
            )
        }
    }
}