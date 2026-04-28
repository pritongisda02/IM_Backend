package fruitylicious.repository.local

import fruitylicious.entity.Inventory
import fruitylicious.entity.InventoryId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface LocalInventoryRepository : JpaRepository<Inventory, InventoryId> {

    fun findAllByBranchId(branchId: Long): List<Inventory>

    fun findByIngredientIdAndBranchId(ingredientId: Long, branchId: Long): Inventory?

    fun findAllByIsSyncedFalse(): List<Inventory>

    @Query(
        """
        SELECT i FROM Inventory i
        WHERE i.branchId = :branchId
          AND i.currentStock <= :threshold
        """
    )
    fun findLowStock(
        @Param("branchId") branchId: Long,
        @Param("threshold") threshold: BigDecimal
    ): List<Inventory>

    @Query(
        """
        SELECT i FROM Inventory i
        WHERE i.currentStock <= :threshold
        """
    )
    fun findLowStockAllBranches(
        @Param("threshold") threshold: BigDecimal
    ): List<Inventory>
}