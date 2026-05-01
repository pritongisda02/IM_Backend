package fruitylicious.repository

import fruitylicious.entity.InventoryAdjustmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface InventoryAdjustmentRepository : JpaRepository<InventoryAdjustmentEntity, Long> {

    fun findAllByBranchBranchIdAndDateTimeBetween(
        branchId: Long,
        from: Instant,
        to: Instant
    ): List<InventoryAdjustmentEntity>
}