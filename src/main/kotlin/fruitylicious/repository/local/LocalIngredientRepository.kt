package fruitylicious.repository.local

import fruitylicious.entity.Ingredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocalIngredientRepository : JpaRepository<Ingredient, Long> {

    fun findByIngredientNameContainingIgnoreCase(name: String): List<Ingredient>

    fun findAllByIsPackaging(isPackaging: Boolean): List<Ingredient>

    fun findAllByIsSyncedFalse(): List<Ingredient>

    fun existsByIngredientNameIgnoreCase(ingredientName: String): Boolean
}