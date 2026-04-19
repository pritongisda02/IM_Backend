package fruitylicious.repository

import fruitylicious.entity.Ingredient
import org.springframework.data.jpa.repository.JpaRepository

interface IngredientRepository : JpaRepository<Ingredient, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<Ingredient>
}