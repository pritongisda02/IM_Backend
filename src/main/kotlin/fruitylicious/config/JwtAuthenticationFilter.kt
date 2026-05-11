package fruitylicious.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (!authHeader.isNullOrBlank() && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ").trim()

            try {
                val claims = jwtService.parseClaims(token)
                val username = claims.subject
                val role = claims["role"]?.toString().orEmpty()

                val authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_${role.uppercase()}"))
                )

                SecurityContextHolder.getContext().authentication = authentication
            } catch (_: Exception) {
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }
}