package fruitylicious.repository.local

import fruitylicious.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocalProductRepository : JpaRepository<Product, Long> {

    fun findByProductNameContainingIgnoreCase(name: String): List<Product>

    fun findAllByIsAddon(isAddon: Boolean): List<Product>

    fun findAllByIsSyncedFalse(): List<Product>

    fun existsByProductNameIgnoreCase(productName: String): Boolean
}