package cz.abo.b2b.web.dao.jdbc

import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet
import javax.sql.DataSource

@Component
open class SupplierJdbcRepository (val jdbcTemplate: JdbcTemplate) {

    fun supplierDetails(): List<SupplierDetails> {
        return jdbcTemplate.query(
            "select supplier_id, name, count(supplier_id) as product_count from supplier s JOIN product p where s.id=p.supplier_id group by s.id",
            arrayOf()
        ) { rs: ResultSet, rowNum: Int ->
            SupplierDetails(
                rs.getString("name"),
                rs.getLong("product_count")
            )
        }
    }

}