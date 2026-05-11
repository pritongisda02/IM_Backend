package fruitylicious.repository

import fruitylicious.entity.InventoryEntity
import fruitylicious.entity.InventoryId
import fruitylicious.repository.report.InventoryReportRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InventoryRepository : JpaRepository<InventoryEntity, InventoryId> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<InventoryEntity>
    fun findByIdBranchId(branchId: Int): List<InventoryEntity>

    @Query(
        """
    SELECT
        i.ingredientId AS ingredientId,
        i.ingredientName AS ingredientName,
        i.unitType AS unitType,
        inv.currentStock AS currentStock,
        i.lowStockThreshold AS lowStockThreshold,
        i.image AS image
    FROM InventoryEntity inv
    JOIN IngredientEntity i ON inv.id.ingredientId = i.ingredientId
    WHERE inv.id.branchId = :branchId
    ORDER BY i.ingredientName ASC
    """
    )
    fun getInventoryReportRows(
        @Param("branchId") branchId: Int
    ): List<InventoryReportRow>

    fun findByIdBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): List<InventoryEntity>

    fun countByIdBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): Long
}