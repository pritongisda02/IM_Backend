package fruitylicious.repository

import fruitylicious.entity.ProductRecipeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ProductRecipeRepository : JpaRepository<ProductRecipeEntity, Long> {

    fun findAllByProductProductId(productId: Long): List<ProductRecipeEntity>

    fun findAllByLastModifiedAfter(since: Instant): List<ProductRecipeEntity>
}