package fruitylicious.repository.oracle

import fruitylicious.entity.Ingredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OracleIngredientRepository : JpaRepository<Ingredient, Long> {

    fun findAllByLastModifiedAfter(lastModified: LocalDateTime): List<Ingredient>

    fun findByIngredientNameIgnoreCase(ingredientName: String): Ingredient?
}