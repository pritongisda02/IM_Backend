package Fruitylicous.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

data class RecipeRequest(
    @field:NotNull(message = "Product ID is required")
    val productId: Long,

    @field:NotNull(message = "Ingredient ID is required")
    val ingredientId: Long,

    @field:NotNull(message = "Quantity required is required")
    @field:DecimalMin(value = "0.0001", message = "Quantity must be greater than zero")
    val quantityRequired: BigDecimal
)

data class RecipeResponse(
    val recipeId: Long,
    val productId: Long,
    val ingredientId: Long,
    val ingredientName: String,
    val unitType: String,
    val quantityRequired: BigDecimal,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)

data class RecipeUpdateRequest(
    @field:NotNull(message = "Quantity required is required")
    @field:DecimalMin(value = "0.0001", message = "Quantity must be greater than zero")
    val quantityRequired: BigDecimal
)