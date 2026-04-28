package fruitylicious.repository

import fruitylicious.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    fun findAllByLastModifiedAfter(since: Instant): List<Product>
}