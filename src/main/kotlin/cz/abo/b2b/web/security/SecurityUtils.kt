package com.example.application.security

import com.vaadin.flow.server.HandlerHelper
import com.vaadin.flow.server.HandlerHelper.RequestType
import com.vaadin.flow.shared.ApplicationConstants
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.util.stream.Stream
import javax.servlet.http.HttpServletRequest


class SecurityUtils {
    companion object {

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