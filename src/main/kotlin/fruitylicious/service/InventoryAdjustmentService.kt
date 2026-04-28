package fruitylicious.service

import fruitylicious.entity.InventoryAdjustment
import fruitylicious.repository.oracle.OracleInventoryAdjustmentRepository
import org.springframework.stereotype.Service

@Service
class InventoryAdjustmentService (
    private val inventoryAdjustmentRepository: OracleInventoryAdjustmentRepository
)
{
    fun createAdjustment(adjust: InventoryAdjustment): InventoryAdjustment{
        return inventoryAdjustmentRepository.save(adjust)
    }

    fun getAllAdjustment(): List<InventoryAdjustment>{
        return inventoryAdjustmentRepository.findAll()
    }
}