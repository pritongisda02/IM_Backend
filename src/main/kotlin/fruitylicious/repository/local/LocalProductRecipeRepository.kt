package fruitylicious.repository.local

import fruitylicious.entity.ProductRecipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocalProductRecipeRepository : JpaRepository<ProductRecipe, Long> {

    fun findAllByProductId(productId: Long): List<ProductRecipe>

    fun findAllByIngredientId(ingredientId: Long): List<ProductRecipe>

    fun findByProductIdAndIngredientId(productId: Long, ingredientId: Long): ProductRecipe?

    fun existsByProductIdAndIngredientId(productId: Long, ingredientId: Long): Boolean

    fun deleteAllByProductId(productId: Long)

    fun findAllByIsSyncedFalse(): List<ProductRecipe>
}