package fruitylicious.dto

import java.time.LocalDateTime

data class AuditLogResponse(
    val logId: Long,
    val userId: Long,
    val userName: String,
    val branchId: Long,
    val action: String,
    val tableAffected: String,
    val details: String?,
    val timestamp: LocalDateTime,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)