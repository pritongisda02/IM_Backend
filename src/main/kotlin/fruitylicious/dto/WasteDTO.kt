package fruitylicious.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

data class WasteRequest(
    @field:NotNull(message = "Ingredient ID is required")
    val ingredientId: Long,

    @field:NotNull(message = "Quantity is required")
    @field:DecimalMin(value = "0.0001", message = "Quantity must be greater than zero")
    val quantity: BigDecimal,

    @field:NotBlank(message = "Reason is required")
    val reason: String
)

data class WasteResponse(
    val wasteId: Long,
    val ingredientId: Long,
    val ingredientName: String,
    val branchId: Long,
    val userId: Long,
    val quantity: BigDecimal,
    val reason: String,
    val dateTime: LocalDateTime,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)