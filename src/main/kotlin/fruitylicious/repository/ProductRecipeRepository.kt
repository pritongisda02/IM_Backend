package fruitylicious.repository

import fruitylicious.entity.ProductRecipeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRecipeRepository : JpaRepository<ProductRecipeEntity, Int> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<ProductRecipeEntity>
}