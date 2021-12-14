package cz.abo.b2b.web.security

import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter
import cz.abo.b2b.web.security.view.LoginView
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager


@EnableWebSecurity
@Configuration
open class SecurityConfiguration : VaadinWebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // Delegating the responsibility of general configurations
        // of http security to the super class. It is configuring
        // the followings: Vaadin's CSRF protection by ignoring
        // framework's internal requests, default request cache,
        // ignoring public views annotated with @AnonymousAllowed,
        // restricting access to other views/endpoints, and enabling
        // ViewAccessChecker authorization.
        // You can add any possible extra configurations of your own
        // here (the following is just an example):

        // http.rememberMe().alwaysRemember(false);
        super.configure(http)

        // This is important to register your login view to the
        // view access checker mechanism:
        setLoginView(http, LoginView::class.java)
    }

    /**
     * Allows access to static resources, bypassing Spring security.
     */
    @Throws(Exception::class)
    override fun configure(web: WebSecurity) {
        // Configure your static resources with public access here:
        web.ignoring().antMatchers(
            "/images/**"
        )

        // Delegating the ignoring configuration for Vaadin's
        // related static resources to the super class:
        super.configure(web)
    }

    /**
     * Demo UserDetailService which only provide two hardcoded
     * in memory users and their roles.
     * NOTE: This should not be used in real world applications.
     */
    @Bean
    public override fun userDetailsService(): UserDetailsService {
        val user = User.withUsername("user")
            .password("{noop}user")
            .roles("USER")
            .build()
        val admin = User.withUsername("admin")
            .password("{noop}admin")
            .roles("ADMIN")
            .build()
        return InMemoryUserDetailsManager(user, admin)
    }
}