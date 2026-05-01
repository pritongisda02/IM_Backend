package fruitylicious.repository

import fruitylicious.entity.InventoryEntity
import fruitylicious.entity.InventoryId
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryRepository : JpaRepository<InventoryEntity, InventoryId> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<InventoryEntity>
    fun findByIdBranchId(branchId: Int): List<InventoryEntity>
}