package fruitylicious.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant

data class IngredientRequest(
    val ingredientId: Long = 0,
    val image: String? = null,

    @field:NotBlank(message = "Ingredient name is required")
    val ingredientName: String,

    @field:NotBlank(message = "Unit type is required")
    val unitType: String,

    val estimatedWeightPerUnit: BigDecimal? = null,
    val isPackaging: Boolean = false,

    @field:NotNull(message = "Low stock threshold is required")
    val lowStockThreshold: BigDecimal
)

data class IngredientResponse(
    val ingredientId: Long,
    val image: String?,
    val ingredientName: String,
    val unitType: String,
    val estimatedWeightPerUnit: BigDecimal?,
    val isPackaging: Boolean,
    val lowStockThreshold: BigDecimal,
    val lastModified: Instant
)