package fruitylicious.dto

import java.math.BigDecimal
import java.time.Instant

data class InventoryRequest(
    val ingredientId: Long,
    val branchId: Long,
    val currentStock: BigDecimal
)

data class InventoryResponse(
    val ingredientId: Long,
    val ingredientName: String,
    val branchId: Long,
    val currentStock: BigDecimal,
    val unitType: String,
    val lowStockThreshold: BigDecimal,
    val lastModified: Instant
)