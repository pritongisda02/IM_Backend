package fruitylicious.dto

import java.time.Instant

data class StaffLogRequest(
    val logId: Long = 0,
    val userId: Long,
    val branchId: Long,
    val image: String? = null,
    val clockIn: Instant,
    val clockOut: Instant? = null
)

data class StaffLogResponse(
    val logId: Long,
    val userId: Long,
    val userName: String,
    val branchId: Long,
    val image: String?,
    val clockIn: Instant,
    val clockOut: Instant?,
    val lastModified: Instant
)