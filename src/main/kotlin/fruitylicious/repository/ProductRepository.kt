package fruitylicious.repository

import fruitylicious.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ProductRepository : JpaRepository<ProductEntity, Long> {

    fun findAllByLastModifiedAfter(since: Instant): List<ProductEntity>
}