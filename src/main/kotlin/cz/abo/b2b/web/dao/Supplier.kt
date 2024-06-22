package cz.abo.b2b.web.dao

import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.ImportSourceType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class Supplier(

    var name: String,

    /**
     * Free transport, amount without VAT
     */
    var freeTransportFrom: BigDecimal,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var products: MutableList<Product> = ArrayList(),

    @Column(columnDefinition = "LONGTEXT")
    var description: String?,
    var importUrl: String,
    var importerClassName: String,
    var orderEmail: String,
    var lastImport: LocalDateTime? = null
) {




    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier_seq")
    val id: Long = 0

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

    fun addProduct(product: Product) {
        products.add(product)
        product.supplier = this
    }

    fun removeProduct(product: Product) {
        products.remove(product)
        product.supplier = null
    }
}
