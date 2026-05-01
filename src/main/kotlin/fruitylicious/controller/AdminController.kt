package fruitylicious.controller

import fruitylicious.dto.BranchRequest
import fruitylicious.dto.BranchResponse
import fruitylicious.dto.UserRequest
import fruitylicious.dto.UserResponse
import fruitylicious.service.AdminService
import jakarta.validation.Valid
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

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val adminService: AdminService
) {

    // ── Branches ──────────────────────────────────────────────────────────────

    @GetMapping("/branches")
    fun getAllBranches(): ResponseEntity<List<BranchResponse>> =
        ResponseEntity.ok(adminService.getAllBranches())

    @PostMapping("/branches")
    fun createBranch(
        @Valid @RequestBody request: BranchRequest
    ): ResponseEntity<BranchResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(adminService.createBranch(request))

    @PutMapping("/branches/{id}")
    fun updateBranch(
        @PathVariable id: Long,
        @Valid @RequestBody request: BranchRequest
    ): ResponseEntity<BranchResponse> =
        ResponseEntity.ok(adminService.updateBranch(id, request))

    // ── Users ─────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<UserResponse>> =
        ResponseEntity.ok(adminService.getAllUsers())

    @PostMapping("/users")
    fun createUser(
        @Valid @RequestBody request: UserRequest
    ): ResponseEntity<UserResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(adminService.createUser(request))

    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UserRequest
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(adminService.updateUser(id, request))

    @DeleteMapping("/users/{id}")
    fun deleteUser(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        adminService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}