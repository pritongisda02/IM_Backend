package fruitylicious.repository.local

import fruitylicious.entity.InventoryAdjustment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LocalInventoryAdjustmentRepository : JpaRepository<InventoryAdjustment, Long> {

    fun findAllByBranchId(branchId: Long): List<InventoryAdjustment>

    fun findAllByBranchIdAndIngredientId(
        branchId: Long,
        ingredientId: Long
    ): List<InventoryAdjustment>

    fun findAllByBranchIdAndDateTimeBetween(
        branchId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<InventoryAdjustment>

    fun findAllByIsSyncedFalse(): List<InventoryAdjustment>
}