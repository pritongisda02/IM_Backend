package fruitylicious.dto

import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class UserRequest(
    val userId: Long = 0,

    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Role is required")
    val role: String,

    @field:NotBlank(message = "Username is required")
    val username: String,

    val password: String? = null
)

data class UserResponse(
    val userId: Long,
    val name: String,
    val role: String,
    val username: String,
    val lastModified: Instant
)