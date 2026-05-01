package fruitylicious.repository

import fruitylicious.entity.ProductRecipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ProductRecipeRepository : JpaRepository<ProductRecipe, Long> {

    fun findAllByProductProductId(productId: Long): List<ProductRecipe>

    fun findAllByLastModifiedAfter(since: Instant): List<ProductRecipe>
}