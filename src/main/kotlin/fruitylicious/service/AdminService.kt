package fruitylicious.service

import fruitylicious.dto.*
import fruitylicious.entity.Branch
import fruitylicious.entity.User
import fruitylicious.repository.BranchRepository
import fruitylicious.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AdminService(
    private val branchRepository: BranchRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // ── Branches ──────────────────────────────────────────────────────────────

    fun getAllBranches(): List<BranchResponse> =
        branchRepository.findAll().map { it.toResponse() }

    @Transactional
    fun createBranch(request: BranchRequest): BranchResponse {
        val entity = Branch().apply {
            branchId = request.branchId
            branchName = request.branchName
            address = request.address
            contactNumber = request.contactNumber
            lastModified = Instant.now()
        }
        return branchRepository.save(entity).toResponse()
    }

    @Transactional
    fun updateBranch(id: Long, request: BranchRequest): BranchResponse {
        val entity = branchRepository.findById(id)
            .orElseThrow { NoSuchElementException("Branch $id not found.") }

        entity.branchName = request.branchName
        entity.address = request.address
        entity.contactNumber = request.contactNumber
        entity.lastModified = Instant.now()

        return branchRepository.save(entity).toResponse()
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    fun getAllUsers(): List<UserResponse> =
        userRepository.findAll().map { it.toResponse() }

    @Transactional
    fun createUser(request: UserRequest): UserResponse {
        val rawPassword = request.password
            ?: throw IllegalArgumentException("Password is required when creating a user.")

        val entity = User().apply {
            userId = request.userId
            name = request.name
            role = request.role
            username = request.username
            password = passwordEncoder.encode(rawPassword)
            lastModified = Instant.now()
        }
        return userRepository.save(entity).toResponse()
    }

    @Transactional
    fun updateUser(id: Long, request: UserRequest): UserResponse {
        val entity = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("User $id not found.") }

        entity.name = request.name
        entity.role = request.role
        entity.username = request.username

        if (!request.password.isNullOrBlank()) {
            entity.password = passwordEncoder.encode(request.password)
        }

        entity.lastModified = Instant.now()
        return userRepository.save(entity).toResponse()
    }

    @Transactional
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw NoSuchElementException("User $id not found.")
        }
        userRepository.deleteById(id)
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private fun Branch.toResponse() = BranchResponse(
        branchId = branchId,
        branchName = branchName,
        address = address,
        contactNumber = contactNumber,
        lastModified = lastModified
    )

    private fun User.toResponse() = UserResponse(
        userId = userId,
        name = name,
        role = role,
        username = username,
        lastModified = lastModified
    )
}