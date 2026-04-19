package fruitylicious.service

import fruitylicious.entity.InventoryAdjustment
import fruitylicious.repository.InventoryAdjustmentRepository
import org.springframework.stereotype.Service

@Service
class InventoryAdjustmentService (
    private val inventoryAdjustmentRepository: InventoryAdjustmentRepository
)
{
    fun createAdjustment(adjust: InventoryAdjustment): InventoryAdjustment{
        return inventoryAdjustmentRepository.save(adjust)
    }

    fun getAllAdjustment(): List<InventoryAdjustment>{
        return inventoryAdjustmentRepository.findAll()
    }
}