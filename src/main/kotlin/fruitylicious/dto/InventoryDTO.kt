package Fruitylicous.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

// --- Inventory state ---

data class InventoryResponse(
    val ingredientId: Long,
    val ingredientName: String,
    val unitType: String,
    val branchId: Long,
    val currentStock: BigDecimal,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)

// --- Restock ---

data class RestockRequest(
    @field:NotNull(message = "Ingredient ID is required")
    val ingredientId: Long,

    @field:NotNull(message = "Quantity added is required")
    @field:DecimalMin(value = "0.0001", message = "Quantity must be greater than zero")
    val quantityAdded: BigDecimal,

    val supplier: String? = null
)

data class RestockResponse(
    val restockId: Long,
    val ingredientId: Long,
    val ingredientName: String,
    val branchId: Long,
    val userId: Long,
    val quantityAdded: BigDecimal,
    val supplier: String?,
    val dateTime: LocalDateTime,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)

// --- Adjustment ---

data class AdjustmentRequest(
    @field:NotNull(message = "Ingredient ID is required")
    val ingredientId: Long,

    @field:NotNull(message = "Adjustment amount is required")
    val adjustmentAmount: BigDecimal,    // positive or negative

    @field:NotBlank(message = "Reason is required")
    val reason: String
)

data class AdjustmentResponse(
    val adjustmentId: Long,
    val ingredientId: Long,
    val ingredientName: String,
    val branchId: Long,
    val userId: Long,
    val adjustmentAmount: BigDecimal,
    val reason: String,
    val dateTime: LocalDateTime,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)