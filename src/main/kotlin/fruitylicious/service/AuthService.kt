package fruitylicious.service

import fruitylicious.config.JwtTokenProvider
import fruitylicious.dto.LoginRequest
import fruitylicious.dto.LoginResponse
import fruitylicious.entity.User
import fruitylicious.repository.local.LocalUserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val localUserRepository: LocalUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${app.jwt.expiration-ms}") private val expirationMs: Long,
    @Value("\${app.branch-id}") private val deviceBranchId: Long
) {

    /**
     * Authenticate a user against the local SQLite Users table.
     * No internet connection is required — JWT is generated locally.
     */
    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        val user = localUserRepository.findByUsername(request.username)
            ?: throw BadCredentialsException("Invalid username or password")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BadCredentialsException("Invalid username or password")
        }

        // Admin has no branch restriction; staff is bound to the device branch
        val effectiveBranchId: Long? = when (user.role.lowercase()) {
            "admin" -> null
            else    -> deviceBranchId
        }

        val token = jwtTokenProvider.generateToken(
            username  = user.username,
            role      = user.role,
            userId    = user.userId,
            branchId  = effectiveBranchId
        )

        return LoginResponse(
            token       = token,
            userId      = user.userId,
            name        = user.name,
            role        = user.role,
            branchId    = effectiveBranchId,
            expiresInMs = expirationMs
        )
    }

    /**
     * Used by LocalUserDetailsService during JWT filter validation.
     */
    @Transactional(readOnly = true)
    fun findByUsername(username: String): User? =
        localUserRepository.findByUsername(username)
}