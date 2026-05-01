package fruitylicious.dto

import java.math.BigDecimal
import java.time.Instant

data class InventoryAdjustmentRequest(
    val adjustmentId: Long = 0,
    val ingredientId: Long,
    val branchId: Long,
    val userId: Long,
    val adjustmentAmount: BigDecimal,
    val reason: String? = null,
    val dateTime: Instant
)

data class InventoryAdjustmentResponse(
    val adjustmentId: Long,
    val ingredientId: Long,
    val ingredientName: String,
    val branchId: Long,
    val userId: Long,
    val userName: String,
    val adjustmentAmount: BigDecimal,
    val reason: String?,
    val dateTime: Instant,
    val lastModified: Instant
)