package fruitylicious.repository

import fruitylicious.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository <Product, Long> {
    fun findByIsAddonFalse(): List<Product>
    fun findByIsAddonTrue(): List<Product>
}