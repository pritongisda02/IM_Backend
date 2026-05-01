package fruitylicious.repository

import fruitylicious.entity.ProductVariantEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductVariantRepository : JpaRepository<ProductVariantEntity, Int> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<ProductVariantEntity>
}