package fruitylicious.dto

import java.math.BigDecimal
import java.time.Instant

data class WasteLogRequest(
    val wasteId: Long = 0,
    val ingredientId: Long,
    val branchId: Long,
    val userId: Long,
    val quantity: BigDecimal,
    val image: String? = null,
    val reason: String? = null,
    val dateTime: Instant
)

data class WasteLogResponse(
    val wasteId: Long,
    val ingredientId: Long,
    val ingredientName: String,
    val branchId: Long,
    val userId: Long,
    val userName: String,
    val quantity: BigDecimal,
    val image: String?,
    val reason: String?,
    val dateTime: Instant,
    val lastModified: Instant
)