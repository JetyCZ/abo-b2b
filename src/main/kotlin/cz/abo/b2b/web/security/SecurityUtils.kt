package com.example.application.security

import com.vaadin.flow.server.HandlerHelper.RequestType
import com.vaadin.flow.shared.ApplicationConstants
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import java.util.stream.Stream
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList


class SecurityUtils {
    companion object {

        fun isAccessGranted(securedClass: Class<*>?): Boolean {
            // Allow if no roles are required.
            val secured: Secured? = AnnotationUtils.findAnnotation(securedClass, Secured::class.java)

            var allowedRoles = ArrayList<String>()
            if (secured!=null) {
                for (value in secured.value) {
                    allowedRoles.add(value)
                }
            }
            // lookup needed role in user roles
            val userAuthentication: Authentication = SecurityContextHolder.getContext().authentication
            return userAuthentication.getAuthorities().stream() // (2)
                .map { obj: GrantedAuthority -> obj.authority }
                .anyMatch(allowedRoles::contains)
        }

        @JvmStatic
        fun isFrameworkInternalRequest(request: HttpServletRequest): Boolean {
            val parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER)
            return (parameterValue != null
                    && Stream.of(*RequestType.values())
                .anyMatch { r: RequestType -> r.identifier == parameterValue })
        }



        @JvmStatic
        fun isUserLoggedIn(): Boolean {
                val authentication = SecurityContextHolder.getContext().authentication
                return (authentication != null && authentication !is AnonymousAuthenticationToken
                        && authentication.isAuthenticated)
            }
    }

}