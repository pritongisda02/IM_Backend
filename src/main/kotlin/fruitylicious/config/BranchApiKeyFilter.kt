package fruitylicious.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class BranchApiKeyFilter(
    @Value("\${app.api-keys.branch-1}")
    private val branch1ApiKey: String,

    @Value("\${app.api-keys.branch-2}")
    private val branch2ApiKey: String
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return !request.requestURI.startsWith("/api/sync")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val branchId = request.getHeader("X-BRANCH-ID")?.toIntOrNull()
        val apiKey = request.getHeader("X-API-KEY")

        val expectedKey = when (branchId) {
            1 -> branch1ApiKey
            2 -> branch2ApiKey
            else -> null
        }

        if (expectedKey == null || apiKey.isNullOrBlank() || apiKey != expectedKey) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.writer.write("""{"message":"Invalid branch API key."}""")
            return
        }

        filterChain.doFilter(request, response)
    }
}