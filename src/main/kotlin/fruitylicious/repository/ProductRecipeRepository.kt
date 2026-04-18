package fruitylicious.repository

import fruitylicious.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRecipeRepository : JpaRepository<Product, Long> {
}