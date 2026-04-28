package fruitylicious.repository.oracle

import fruitylicious.entity.Inventory
import fruitylicious.entity.InventoryId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OracleInventoryRepository : JpaRepository<Inventory, InventoryId> {

    fun findAllByBranchId(branchId: Long): List<Inventory>

    fun findByIngredientIdAndBranchId(ingredientId: Long, branchId: Long): Inventory?

    fun findAllByLastModifiedAfter(lastModified: LocalDateTime): List<Inventory>
}