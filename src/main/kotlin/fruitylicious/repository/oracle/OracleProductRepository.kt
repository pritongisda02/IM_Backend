package fruitylicious.repository.oracle

import fruitylicious.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OracleProductRepository : JpaRepository<Product, Long> {

    fun findAllByLastModifiedAfter(lastModified: LocalDateTime): List<Product>

    fun findByProductNameIgnoreCase(productName: String): Product?
}