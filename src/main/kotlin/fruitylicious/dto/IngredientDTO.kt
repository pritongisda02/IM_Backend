package fruitylicious.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

data class IngredientRequest(
    @field:NotBlank(message = "Ingredient name is required")
    val ingredientName: String,

    val image: String? = null,

    @field:NotBlank(message = "Unit type is required")
    val unitType: String,

    val estimatedWeightPerUnit: BigDecimal? = null,

    @field:NotNull(message = "is_packaging flag is required")
    val isPackaging: Boolean
)

data class IngredientResponse(
    val ingredientId: Long,
    val ingredientName: String,
    val image: String?,
    val unitType: String,
    val estimatedWeightPerUnit: BigDecimal?,
    val isPackaging: Boolean,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)