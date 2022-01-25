package cz.abo.b2b.web.dao.jdbc

import cz.abo.b2b.web.dao.Product
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet
import javax.sql.DataSource

@Component
open class ProductJdbcRepository (val jdbcTemplate: JdbcTemplate) {

    fun products(): List<Product> {
        return ArrayList<Product>()
        /*
        return jdbcTemplate.query(
            "select supplier_id, name, count(supplier_id) as product_count from supplier s JOIN product p where s.id=p.supplier_id group by s.id",
            arrayOf()
        ) { rs: ResultSet, rowNum: Int ->
            SupplierDetails(
                rs.getString("name"),
                rs.getLong("product_count")
            )
        }*/
    }

}