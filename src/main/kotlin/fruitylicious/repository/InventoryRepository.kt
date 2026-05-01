package fruitylicious.repository

import fruitylicious.entity.Inventory
import fruitylicious.entity.InventoryId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface InventoryRepository : JpaRepository<Inventory, InventoryId> {

    fun findAllByBranchId(branchId: Long): List<Inventory>

    fun findAllByLastModifiedAfter(since: Instant): List<Inventory>
}