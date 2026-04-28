package fruitylicious.dto

import java.time.LocalDateTime

data class StaffLogResponse(
    val logId: Long,
    val userId: Long,
    val userName: String,
    val branchId: Long,
    val clockIn: LocalDateTime,
    val clockOut: LocalDateTime?,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)

data class ClockInResponse(
    val logId: Long,
    val userId: Long,
    val branchId: Long,
    val clockIn: LocalDateTime,
    val message: String
)

data class ClockOutResponse(
    val logId: Long,
    val userId: Long,
    val branchId: Long,
    val clockIn: LocalDateTime,
    val clockOut: LocalDateTime,
    val message: String
)