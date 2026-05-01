package fruitylicious.dto

import java.time.Instant

data class AuditLogRequest(
    val logId: Long = 0,
    val userId: Long,
    val branchId: Long,
    val action: String,
    val tableAffected: String,
    val timestamp: Instant
)

data class AuditLogResponse(
    val logId: Long,
    val userId: Long,
    val userName: String,
    val branchId: Long,
    val action: String,
    val tableAffected: String,
    val timestamp: Instant,
    val lastModified: Instant
)