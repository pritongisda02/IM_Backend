package fruitylicious.repository

import fruitylicious.entity.InventoryAdjustment
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryAdjustRepository : JpaRepository<InventoryAdjustment, Long> {
}