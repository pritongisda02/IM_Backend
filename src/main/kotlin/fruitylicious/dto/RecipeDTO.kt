package fruitylicious.dto

import java.math.BigDecimal
import java.time.Instant

data class RecipeRequest(
    val recipeId: Long = 0,
    val productId: Long,
    val ingredientId: Long,
    val quantityRequired: BigDecimal
)

data class RecipeResponse(
    val recipeId: Long,
    val productId: Long,
    val ingredientId: Long,
    val quantityRequired: BigDecimal,
    val lastModified: Instant
)