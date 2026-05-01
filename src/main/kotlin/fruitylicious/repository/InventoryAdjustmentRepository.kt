package fruitylicious.repository

import fruitylicious.entity.InventoryAdjustmentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryAdjustmentRepository : JpaRepository<InventoryAdjustmentEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<InventoryAdjustmentEntity>
}