package fruitylicious.auth

data class LoginRequest(
    val username: String,
    val password: String,
    val branchId: Int
)

data class LoginResponse(
    val token: String,
    val userId: Int,
    val name: String,
    val role: String,
    val username: String,
    val expiresAt: Long
)