package fruitylicious.repository

import fruitylicious.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<ProductEntity, Int> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<ProductEntity>
}