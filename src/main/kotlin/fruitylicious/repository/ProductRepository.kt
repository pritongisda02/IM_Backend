package fruitylicious.repository

import fruitylicious.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository <Product, Long> {
    fun findByIsAddOnFalse(): List<Product>
    fun findByIsAddOnTrue(): List<Product>
}