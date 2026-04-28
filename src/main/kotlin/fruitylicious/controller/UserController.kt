package fruitylicious.controller

import fruitylicious.config.JwtTokenProvider
import fruitylicious.service.UserRequest
import fruitylicious.service.UserResponse
import fruitylicious.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// DTO wrapper for incoming user requests with validation
data class UserRequestBody(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Role is required")
    val role: String,

    @field:NotBlank(message = "Username is required")
    val username: String,

    @field:NotBlank(message = "Password is required")
    val password: String,

    val branchId: Long? = null
)

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    // -------------------------------------------------------------------------
    // GET /api/admin/users
    // -------------------------------------------------------------------------

    @GetMapping
    fun getAll(): ResponseEntity<List<UserResponse>> =
        ResponseEntity.ok(userService.getAll())

    // -------------------------------------------------------------------------
    // GET /api/admin/users/{id}
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getById(id))

    // -------------------------------------------------------------------------
    // POST /api/admin/users
    // -------------------------------------------------------------------------

    @PostMapping
    fun create(
        @Valid @RequestBody body: UserRequestBody,
        httpRequest: HttpServletRequest
    ): ResponseEntity<UserResponse> {
        val (userId, branchId) = resolveUser(httpRequest)
        val request = UserRequest(
            name     = body.name,
            role     = body.role,
            username = body.username,
            password = body.password,
            branchId = body.branchId
        )
        val response = userService.create(request, userId, branchId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // -------------------------------------------------------------------------
    // PUT /api/admin/users/{id}
    // -------------------------------------------------------------------------

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody body: UserRequestBody,
        httpRequest: HttpServletRequest
    ): ResponseEntity<UserResponse> {
        val (userId, branchId) = resolveUser(httpRequest)
        val request = UserRequest(
            name     = body.name,
            role     = body.role,
            username = body.username,
            password = body.password,
            branchId = body.branchId
        )
        val response = userService.update(id, request, userId, branchId)
        return ResponseEntity.ok(response)
    }

    // -------------------------------------------------------------------------
    // DELETE /api/admin/users/{id}
    // -------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        val (userId, branchId) = resolveUser(httpRequest)
        userService.delete(id, userId, branchId)
        return ResponseEntity.ok(mapOf("message" to "User $id deleted successfully"))
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private fun resolveUser(request: HttpServletRequest): Pair<Long, Long> {
        val token    = request.getHeader("Authorization").substring(7)
        val userId   = jwtTokenProvider.getUserIdFromToken(token)
        val branchId = jwtTokenProvider.getBranchIdFromToken(token) ?: 0L
        return Pair(userId, branchId)
    }
}