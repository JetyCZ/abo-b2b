package cz.abo.b2b.web

import cz.abo.b2b.web.dao.ProductRepository
import cz.abo.b2b.web.importer.HeurekaXMLParser
import cz.abo.b2b.web.shoppingcart.ShoppingCart
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.context.annotation.SessionScope

@SpringBootApplication
open class Application {


    @Bean
    open fun loadData(heurekaXMLParser: HeurekaXMLParser, repository: ProductRepository): CommandLineRunner {
        return CommandLineRunner {
            var products = heurekaXMLParser.products()
            // save a couple of customers
            repository.saveAll(products)
        }
    }


    @Bean
    @SessionScope
    open fun sessionScopedBean(): ShoppingCart {
        return ShoppingCart()
    }

    companion object {
        private val log = LoggerFactory.getLogger(Application::class.java)
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java)
        }
    }
}