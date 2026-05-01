package fruitylicious.auth

import fruitylicious.config.JwtService
import fruitylicious.repository.BranchRepository
import fruitylicious.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val branchRepository: BranchRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    fun login(request: LoginRequest): LoginResponse {
        val username = request.username.trim()

        if (username.isBlank() || request.password.isBlank()) {
            throw IllegalArgumentException("Username and password are required.")
        }

        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("Invalid username or password.")

        val branchExists = branchRepository.existsById(request.branchId)

        if (!branchExists) {
            throw IllegalArgumentException("Invalid branch.")
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid username or password.")
        }

        val (token, expiresAt) = jwtService.generateToken(
            userId = user.userId,
            username = user.username,
            role = user.role,
            branchId = request.branchId
        )

        return LoginResponse(
            token = token,
            userId = user.userId,
            name = user.name,
            role = user.role,
            username = user.username,
            expiresAt = expiresAt
        )
    }
}
