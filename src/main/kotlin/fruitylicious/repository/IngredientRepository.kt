package fruitylicious.repository

import fruitylicious.entity.IngredientEntity
import org.springframework.data.jpa.repository.JpaRepository

interface IngredientRepository : JpaRepository<IngredientEntity, Int> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<IngredientEntity>
    fun countByLastModifiedGreaterThan(lastModified: Long): Long
}