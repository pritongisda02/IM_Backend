package fruitylicious.service

import fruitylicious.dto.WasteRequest
import fruitylicious.dto.WasteResponse
import fruitylicious.entity.WasteLog
import fruitylicious.repository.local.LocalIngredientRepository
import fruitylicious.repository.local.LocalWasteLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class WasteService(
    private val localWasteLogRepository: LocalWasteLogRepository,
    private val localIngredientRepository: LocalIngredientRepository,
    private val inventoryService: InventoryService,
    private val auditService: AuditService
) {

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getAll(branchId: Long): List<WasteResponse> =
        localWasteLogRepository.findAllByBranchId(branchId)
            .map { it.toResponse() }

    // -------------------------------------------------------------------------
    // Log Waste
    // -------------------------------------------------------------------------

    @Transactional
    fun logWaste(
        request: WasteRequest,
        userId: Long,
        branchId: Long
    ): WasteResponse {
        // Validate ingredient exists
        val ingredient = localIngredientRepository.findById(request.ingredientId)
            .orElseThrow {
                jakarta.persistence.EntityNotFoundException(
                    "Ingredient not found: ${request.ingredientId}"
                )
            }

        // Deduct from inventory — throws InsufficientStockException if not enough
        inventoryService.deductStock(
            ingredientId = request.ingredientId,
            branchId     = branchId,
            quantity     = request.quantity
        )

        // Persist waste log
        val wasteLog = WasteLog().apply {
            ingredientId  = request.ingredientId
            this.branchId = branchId
            this.userId   = userId
            quantity      = request.quantity
            reason        = request.reason
            dateTime      = LocalDateTime.now()
            lastModified  = LocalDateTime.now()
            isSynced      = false
        }
        val saved = localWasteLogRepository.save(wasteLog)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.LOG_WASTE,
            tableAffected = "waste_logs",
            details       = "Logged waste for '${ingredient.ingredientName}': " +
                    "${request.quantity} ${ingredient.unitType}. " +
                    "Reason: ${request.reason}"
        )

        return saved.toResponse()
    }

    // -------------------------------------------------------------------------
    // Mapper
    // -------------------------------------------------------------------------

    private fun WasteLog.toResponse(): WasteResponse {
        val ingredient = localIngredientRepository.findById(ingredientId).orElse(null)
        return WasteResponse(
            wasteId        = wasteId,
            ingredientId   = ingredientId,
            ingredientName = ingredient?.ingredientName ?: "Unknown",
            branchId       = branchId,
            userId         = userId,
            quantity       = quantity,
            reason         = reason,
            dateTime       = dateTime,
            lastModified   = lastModified,
            isSynced       = isSynced,
            syncedAt       = syncedAt
        )
    }
}