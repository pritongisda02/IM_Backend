package fruitylicious.repository

import fruitylicious.entity.InventoryAdjustment
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryAdjustmentRepository : JpaRepository<InventoryAdjustment, Long> {
}