package cz.abo.b2b.web

import cz.abo.b2b.web.state.order.Order
import cz.abo.b2b.web.state.shoppingcart.ShoppingCart
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.context.annotation.SessionScope

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
open class Application {
    companion object {
        private val log = LoggerFactory.getLogger(Application::class.java)
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java)
        }
    }

    @Bean
    @SessionScope
    open fun sessionScopedBean(): ShoppingCart {
        return ShoppingCart()
    }

    @Bean
    @SessionScope
    open fun order(): Order {
        return Order(null)
    }

}