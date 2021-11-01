package cz.abo.b2b.web

import cz.abo.b2b.web.dao.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.math.BigDecimal

@SpringBootApplication
open class Application {
    @Bean
    open fun loadData(repository: ProductRepository): CommandLineRunner {
        return CommandLineRunner {
            // save a couple of customers
            repository.save(Product("Kešu", BigDecimal.valueOf(25.5), null))
            repository.save(Product("Sušené banány", BigDecimal.valueOf(11), null))

        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Application::class.java)
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java)
        }
    }
}