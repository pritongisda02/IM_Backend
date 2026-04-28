package fruitylicious.repository.oracle

import fruitylicious.entity.ProductRecipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OracleProductRecipeRepository : JpaRepository<ProductRecipe, Long> {

    fun findAllByProductId(productId: Long): List<ProductRecipe>

    fun findAllByIngredientId(ingredientId: Long): List<ProductRecipe>

    fun findAllByLastModifiedAfter(lastModified: LocalDateTime): List<ProductRecipe>

    fun findByProductIdAndIngredientId(productId: Long, ingredientId: Long): ProductRecipe?

    fun deleteAllByProductId(productId: Long)
}