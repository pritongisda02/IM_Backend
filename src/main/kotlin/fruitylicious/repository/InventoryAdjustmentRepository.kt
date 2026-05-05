package fruitylicious.repository

import fruitylicious.entity.InventoryAdjustmentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryAdjustmentRepository : JpaRepository<InventoryAdjustmentEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<InventoryAdjustmentEntity>

    fun findByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): List<InventoryAdjustmentEntity>
    fun countByLastModifiedGreaterThan(lastModified: Long): Long

    fun countByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): Long
}