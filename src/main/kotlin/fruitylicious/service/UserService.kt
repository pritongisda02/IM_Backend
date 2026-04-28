package fruitylicious.service

import fruitylicious.config.DuplicateResourceException
import fruitylicious.dto.AuditLogResponse
import fruitylicious.entity.User
import fruitylicious.repository.local.LocalAuditLogRepository
import fruitylicious.repository.local.LocalUserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

data class UserRequest(
    val name: String,
    val role: String,
    val username: String,
    val password: String,
    val branchId: Long? = null
)

data class UserResponse(
    val userId: Long,
    val name: String,
    val role: String,
    val username: String,
    val branchId: Long?,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)

@Service
class UserService(
    private val localUserRepository: LocalUserRepository,
    private val localAuditLogRepository: LocalAuditLogRepository,
    private val passwordEncoder: PasswordEncoder,
    private val auditService: AuditService
) {

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getAll(): List<UserResponse> =
        localUserRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(userId: Long): UserResponse =
        localUserRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found: $userId") }
            .toResponse()

    @Transactional(readOnly = true)
    fun getAuditLogs(branchId: Long?, isAdmin: Boolean): List<AuditLogResponse> {
        val logs = if (isAdmin && branchId == null) {
            localAuditLogRepository.findAll()
        } else {
            localAuditLogRepository.findAllByBranchId(branchId!!)
        }
        return logs.map { log ->
            val user = localUserRepository.findById(log.userId).orElse(null)
            AuditLogResponse(
                logId         = log.logId,
                userId        = log.userId,
                userName      = user?.name ?: "Unknown",
                branchId      = log.branchId,
                action        = log.action,
                tableAffected = log.tableAffected,
                details       = log.details,
                timestamp     = log.timestamp,
                lastModified  = log.lastModified,
                isSynced      = log.isSynced,
                syncedAt      = log.syncedAt
            )
        }
    }

    // -------------------------------------------------------------------------
    // Writes
    // -------------------------------------------------------------------------

    @Transactional
    fun create(
        request: UserRequest,
        actingUserId: Long,
        actingBranchId: Long
    ): UserResponse {
        if (localUserRepository.existsByUsername(request.username)) {
            throw DuplicateResourceException("Username already taken: ${request.username}")
        }

        if (request.role !in listOf("admin", "staff")) {
            throw IllegalArgumentException("Role must be 'admin' or 'staff'")
        }

        val user = User().apply {
            name         = request.name
            role         = request.role.lowercase()
            username     = request.username
            password     = passwordEncoder.encode(request.password)
            branchId     = request.branchId
            lastModified = LocalDateTime.now()
            isSynced     = false
        }
        val saved = localUserRepository.save(user)

        auditService.log(
            userId        = actingUserId,
            branchId      = actingBranchId,
            action        = AuditAction.ADD_USER,
            tableAffected = "users",
            details       = "Created user '${saved.username}' (id=${saved.userId}), role=${saved.role}"
        )

        return saved.toResponse()
    }

    @Transactional
    fun update(
        userId: Long,
        request: UserRequest,
        actingUserId: Long,
        actingBranchId: Long
    ): UserResponse {
        val user = localUserRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found: $userId") }

        // Check username uniqueness only if it changed
        if (user.username != request.username &&
            localUserRepository.existsByUsername(request.username)
        ) {
            throw DuplicateResourceException("Username already taken: ${request.username}")
        }

        if (request.role !in listOf("admin", "staff")) {
            throw IllegalArgumentException("Role must be 'admin' or 'staff'")
        }

        user.apply {
            name         = request.name
            role         = request.role.lowercase()
            username     = request.username
            password     = passwordEncoder.encode(request.password)
            branchId     = request.branchId
            lastModified = LocalDateTime.now()
            isSynced     = false
        }
        val saved = localUserRepository.save(user)

        auditService.log(
            userId        = actingUserId,
            branchId      = actingBranchId,
            action        = AuditAction.UPDATE_USER,
            tableAffected = "users",
            details       = "Updated user '${saved.username}' (id=$userId)"
        )

        return saved.toResponse()
    }

    @Transactional
    fun delete(
        userId: Long,
        actingUserId: Long,
        actingBranchId: Long
    ) {
        val user = localUserRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found: $userId") }

        if (userId == actingUserId) {
            throw IllegalArgumentException("You cannot delete your own account")
        }

        localUserRepository.delete(user)

        auditService.log(
            userId        = actingUserId,
            branchId      = actingBranchId,
            action        = AuditAction.DELETE_USER,
            tableAffected = "users",
            details       = "Deleted user '${user.username}' (id=$userId)"
        )
    }

    // -------------------------------------------------------------------------
    // Mapper
    // -------------------------------------------------------------------------

    private fun User.toResponse() = UserResponse(
        userId       = userId,
        name         = name,
        role         = role,
        username     = username,
        branchId     = branchId,
        lastModified = lastModified,
        isSynced     = isSynced,
        syncedAt     = syncedAt
    )
}