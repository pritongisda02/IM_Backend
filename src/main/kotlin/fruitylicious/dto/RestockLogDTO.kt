package fruitylicious.dto

import java.math.BigDecimal
import java.time.Instant

data class RestockLogRequest(
    val restockId: Long = 0,
    val ingredientId: Long,
    val branchId: Long,
    val userId: Long,
    val quantityAdded: BigDecimal,
    val supplier: String? = null,
    val dateTime: Instant
)

data class RestockLogResponse(
    val restockId: Long,
    val ingredientId: Long,
    val ingredientName: String,
    val branchId: Long,
    val userId: Long,
    val userName: String,
    val quantityAdded: BigDecimal,
    val supplier: String?,
    val dateTime: Instant,
    val lastModified: Instant
)