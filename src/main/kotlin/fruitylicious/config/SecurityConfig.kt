package fruitylicious.config

import Fruitylicous.service.AuthService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

// ---------------------------------------------------------------------------
// JWT Token Provider
// ---------------------------------------------------------------------------

@Component
class JwtTokenProvider(
    @Value("\${app.jwt.secret}") private val jwtSecret: String,
    @Value("\${app.jwt.expiration-ms}") private val jwtExpirationMs: Long
) {
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray(StandardCharsets.UTF_8))
    }

    fun generateToken(username: String, role: String, userId: Long, branchId: Long?): String {
        val now = Date()
        val expiry = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .claim("userId", userId)
            .claim("branchId", branchId)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            parseClaims(token)
            true
        } catch (ex: ExpiredJwtException) { false }
        catch (ex: MalformedJwtException) { false }
        catch (ex: UnsupportedJwtException) { false }
        catch (ex: IllegalArgumentException) { false }
    }

    fun getUsernameFromToken(token: String): String =
        parseClaims(token).subject

    fun getRoleFromToken(token: String): String =
        parseClaims(token)["role"] as String

    fun getUserIdFromToken(token: String): Long =
        (parseClaims(token)["userId"] as Number).toLong()

    fun getBranchIdFromToken(token: String): Long? =
        (parseClaims(token)["branchId"] as? Number)?.toLong()

    private fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
}

// ---------------------------------------------------------------------------
// JWT Authentication Filter
// ---------------------------------------------------------------------------

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    @Lazy private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            val username = jwtTokenProvider.getUsernameFromToken(token)
            val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)
            val authentication = UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.authorities
            )
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearer = request.getHeader("Authorization") ?: return null
        return if (bearer.startsWith("Bearer ")) bearer.substring(7) else null
    }
}

// ---------------------------------------------------------------------------
// UserDetailsService — loads from local SQLite via AuthService
// ---------------------------------------------------------------------------

@Component
class LocalUserDetailsService(
    @Lazy private val authService: AuthService
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = authService.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            listOf(SimpleGrantedAuthority("ROLE_${user.role.uppercase()}"))
        )
    }
}

// ---------------------------------------------------------------------------
// Security Filter Chain
// ---------------------------------------------------------------------------

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // Public
                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                    // Admin-only endpoints
                    .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/ingredients").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/ingredients/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/ingredients/**").hasRole("ADMIN")
                    .requestMatchers("/api/recipes/**").hasRole("ADMIN")
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/sync/trigger").hasRole("ADMIN")

                    // Everything else requires authentication
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}