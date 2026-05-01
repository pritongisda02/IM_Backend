package fruitylicious.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyAuthFilter(
    @Value("\${app.api-keys.branch-1}") private val branch1ApiKey: String,
    @Value("\${app.api-keys.branch-2}") private val branch2ApiKey: String
) : OncePerRequestFilter() {

    private val validApiKeys: Set<String> by lazy {
        setOf(branch1ApiKey, branch2ApiKey)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!request.requestURI.startsWith("/api/sync/")) {
            filterChain.doFilter(request, response)
            return
        }

        val apiKey = request.getHeader("X-API-Key")

        if (apiKey == null || apiKey !in validApiKeys) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = "application/json"
            response.writer.write("""{"error":"Unauthorized","message":"Invalid or missing API key."}""")
            return
        }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_BRANCH"))
        val authentication = UsernamePasswordAuthenticationToken(
            "branch-device",
            null,
            authorities
        )
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }
}