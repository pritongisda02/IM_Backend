package fruitylicious.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${app.jwt.secret}")
    private val secret: String,

    @Value("\${app.jwt.expiration-ms}")
    private val expirationMs: Long
) {
    private val key: SecretKey
        get() = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun generateToken(
        userId: Int,
        username: String,
        role: String,
        branchId: Int
    ): Pair<String, Long> {
        val now = System.currentTimeMillis()
        val expiresAt = now + expirationMs

        val token = Jwts.builder()
            .subject(username)
            .claim("userId", userId)
            .claim("role", role)
            .claim("branchId", branchId)
            .issuedAt(Date(now))
            .expiration(Date(expiresAt))
            .signWith(key)
            .compact()

        return token to expiresAt
    }

    fun parseClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}