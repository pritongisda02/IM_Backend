package fruitylicious.service

import fruitylicious.auth.LoginRequest
import fruitylicious.auth.LoginResponse
import fruitylicious.repository.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${app.jwt.secret}") private val jwtSecret: String,
    @Value("\${app.jwt.expiration-ms}") private val jwtExpirationMs: Long
) {

    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByUsername(request.username)
            .orElseThrow { BadCredentialsException("Invalid username or password.") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BadCredentialsException("Invalid username or password.")
        }

        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray(StandardCharsets.UTF_8))
        val now = System.currentTimeMillis()

        val token = Jwts.builder()
            .subject(user.userId.toString())
            .claim("role", user.role)
            .claim("name", user.name)
            .issuedAt(Date(now))
            .expiration(Date(now + jwtExpirationMs))
            .signWith(key)
            .compact()

        return LoginResponse(
            token = token,
            userId = user.userId,
            name = user.name,
            role = user.role
        )
    }

    fun parseToken(token: String): Pair<Long, String> {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray(StandardCharsets.UTF_8))
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        val userId = claims.subject.toLong()
        val role = claims.get("role", String::class.java)
        return Pair(userId, role)
    }
}