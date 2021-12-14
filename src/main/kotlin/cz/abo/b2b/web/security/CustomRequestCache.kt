package cz.abo.b2b.web.security

import com.example.application.security.SecurityUtils.Companion.isFrameworkInternalRequest
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class CustomRequestCache : HttpSessionRequestCache() {
    override fun saveRequest(request: HttpServletRequest, response: HttpServletResponse) {
        if (!isFrameworkInternalRequest(request)) {
            super.saveRequest(request, response)
        }
    }
}