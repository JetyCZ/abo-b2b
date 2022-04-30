package cz.abo.b2b.web.dao.jdbc

import cz.abo.b2b.web.importer.dto.ImportSource
import cz.abo.b2b.web.importer.dto.ImportSourceType
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

class SupplierDetails(val name: String, val productCount: Long)