package fruitylicious.repository

import fruitylicious.entity.IngredientEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface IngredientRepository : JpaRepository<IngredientEntity, Long> {

    fun findAllByLastModifiedAfter(since: Instant): List<IngredientEntity>
}