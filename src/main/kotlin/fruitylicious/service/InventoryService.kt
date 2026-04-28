package fruitylicious.service

import fruitylicious.config.InsufficientStockException
import fruitylicious.dto.AdjustmentRequest
import fruitylicious.dto.AdjustmentResponse
import fruitylicious.dto.InventoryResponse
import fruitylicious.dto.RestockRequest
import fruitylicious.dto.RestockResponse
import fruitylicious.entity.Inventory
import fruitylicious.entity.InventoryAdjustment
import fruitylicious.entity.RestockLog
import fruitylicious.repository.local.LocalIngredientRepository
import fruitylicious.repository.local.LocalInventoryAdjustmentRepository
import fruitylicious.repository.local.LocalInventoryRepository
import fruitylicious.repository.local.LocalRestockLogRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class InventoryService(
    private val localInventoryRepository: LocalInventoryRepository,
    private val localRestockLogRepository: LocalRestockLogRepository,
    private val localInventoryAdjustmentRepository: LocalInventoryAdjustmentRepository,
    private val localIngredientRepository: LocalIngredientRepository,
    private val auditService: AuditService
) {

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getInventory(branchId: Long): List<InventoryResponse> =
        localInventoryRepository.findAllByBranchId(branchId)
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getLowStock(branchId: Long, threshold: BigDecimal): List<InventoryResponse> =
        localInventoryRepository.findLowStock(branchId, threshold)
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getRestockHistory(branchId: Long): List<RestockResponse> =
        localRestockLogRepository.findAllByBranchId(branchId)
            .map { it.toRestockResponse() }

    // -------------------------------------------------------------------------
    // Restock
    // -------------------------------------------------------------------------

    @Transactional
    fun restock(
        request: RestockRequest,
        userId: Long,
        branchId: Long
    ): RestockResponse {
        val ingredient = localIngredientRepository.findById(request.ingredientId)
            .orElseThrow { EntityNotFoundException("Ingredient not found: ${request.ingredientId}") }

        // Get or create inventory row
        val inventory = localInventoryRepository
            .findByIngredientIdAndBranchId(request.ingredientId, branchId)
            ?: Inventory().apply {
                this.ingredientId = request.ingredientId
                this.branchId     = branchId
                this.currentStock = BigDecimal.ZERO
            }

        inventory.apply {
            currentStock = currentStock.add(request.quantityAdded)
            lastModified = LocalDateTime.now()
            isSynced     = false
        }
        localInventoryRepository.save(inventory)

        val log = RestockLog().apply {
            ingredientId  = request.ingredientId
            this.branchId = branchId
            this.userId   = userId
            quantityAdded = request.quantityAdded
            supplier      = request.supplier
            dateTime      = LocalDateTime.now()
            lastModified  = LocalDateTime.now()
            isSynced      = false
        }
        val savedLog = localRestockLogRepository.save(log)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.RESTOCK_INGREDIENT,
            tableAffected = "restock_logs",
            details       = "Restocked ingredient '${ingredient.ingredientName}' " +
                    "+${request.quantityAdded} ${ingredient.unitType}" +
                    (request.supplier?.let { ", supplier=$it" } ?: "")
        )

        return savedLog.toRestockResponse()
    }

    // -------------------------------------------------------------------------
    // Adjustment
    // -------------------------------------------------------------------------

    @Transactional
    fun adjust(
        request: AdjustmentRequest,
        userId: Long,
        branchId: Long
    ): AdjustmentResponse {
        val ingredient = localIngredientRepository.findById(request.ingredientId)
            .orElseThrow { EntityNotFoundException("Ingredient not found: ${request.ingredientId}") }

        val inventory = localInventoryRepository
            .findByIngredientIdAndBranchId(request.ingredientId, branchId)
            ?: Inventory().apply {
                this.ingredientId = request.ingredientId
                this.branchId     = branchId
                this.currentStock = BigDecimal.ZERO
            }

        val newStock = inventory.currentStock.add(request.adjustmentAmount)
        if (newStock < BigDecimal.ZERO) {
            throw InsufficientStockException(
                "Adjustment would result in negative stock for " +
                        "'${ingredient.ingredientName}'. Current: ${inventory.currentStock}, " +
                        "Adjustment: ${request.adjustmentAmount}"
            )
        }

        inventory.apply {
            currentStock = newStock
            lastModified = LocalDateTime.now()
            isSynced     = false
        }
        localInventoryRepository.save(inventory)

        val adjustment = InventoryAdjustment().apply {
            ingredientId     = request.ingredientId
            this.branchId    = branchId
            this.userId      = userId
            adjustmentAmount = request.adjustmentAmount
            reason           = request.reason
            dateTime         = LocalDateTime.now()
            lastModified     = LocalDateTime.now()
            isSynced         = false
        }
        val savedAdjustment = localInventoryAdjustmentRepository.save(adjustment)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.ADJUST_INVENTORY,
            tableAffected = "inventory_adjustments",
            details       = "Adjusted '${ingredient.ingredientName}' by " +
                    "${request.adjustmentAmount} ${ingredient.unitType}. " +
                    "Reason: ${request.reason}"
        )

        return savedAdjustment.toAdjustmentResponse()
    }

    // -------------------------------------------------------------------------
    // Internal stock deduction — called by TransactionService and WasteService
    // -------------------------------------------------------------------------

    @Transactional
    fun deductStock(
        ingredientId: Long,
        branchId: Long,
        quantity: BigDecimal
    ) {
        val inventory = localInventoryRepository
            .findByIngredientIdAndBranchId(ingredientId, branchId)
            ?: throw InsufficientStockException(
                "No inventory record found for ingredient $ingredientId at branch $branchId"
            )

        if (inventory.currentStock < quantity) {
            val ingredient = localIngredientRepository.findById(ingredientId).orElse(null)
            throw InsufficientStockException(
                "Insufficient stock for '${ingredient?.ingredientName ?: ingredientId}'. " +
                        "Required: $quantity, Available: ${inventory.currentStock}"
            )
        }

        inventory.apply {
            currentStock = currentStock.subtract(quantity)
            lastModified = LocalDateTime.now()
            isSynced     = false
        }
        localInventoryRepository.save(inventory)
    }

    // -------------------------------------------------------------------------
    // Stock validation — called before committing a transaction
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun validateStock(
        ingredientId: Long,
        branchId: Long,
        requiredQuantity: BigDecimal
    ) {
        val inventory = localInventoryRepository
            .findByIngredientIdAndBranchId(ingredientId, branchId)

        val currentStock = inventory?.currentStock ?: BigDecimal.ZERO

        if (currentStock < requiredQuantity) {
            val ingredient = localIngredientRepository.findById(ingredientId).orElse(null)
            throw InsufficientStockException(
                "Insufficient stock for '${ingredient?.ingredientName ?: ingredientId}'. " +
                        "Required: $requiredQuantity, Available: $currentStock"
            )
        }
    }

    // -------------------------------------------------------------------------
    // Mappers
    // -------------------------------------------------------------------------

    private fun Inventory.toResponse(): InventoryResponse {
        val ingredient = localIngredientRepository.findById(ingredientId).orElse(null)
        return InventoryResponse(
            ingredientId   = ingredientId,
            ingredientName = ingredient?.ingredientName ?: "Unknown",
            unitType       = ingredient?.unitType ?: "",
            branchId       = branchId,
            currentStock   = currentStock,
            lastModified   = lastModified,
            isSynced       = isSynced,
            syncedAt       = syncedAt
        )
    }

    private fun RestockLog.toRestockResponse(): RestockResponse {
        val ingredient = localIngredientRepository.findById(ingredientId).orElse(null)
        return RestockResponse(
            restockId      = restockId,
            ingredientId   = ingredientId,
            ingredientName = ingredient?.ingredientName ?: "Unknown",
            branchId       = branchId,
            userId         = userId,
            quantityAdded  = quantityAdded,
            supplier       = supplier,
            dateTime       = dateTime,
            lastModified   = lastModified,
            isSynced       = isSynced,
            syncedAt       = syncedAt
        )
    }

    private fun InventoryAdjustment.toAdjustmentResponse(): AdjustmentResponse {
        val ingredient = localIngredientRepository.findById(ingredientId).orElse(null)
        return AdjustmentResponse(
            adjustmentId   = adjustmentId,
            ingredientId   = ingredientId,
            ingredientName = ingredient?.ingredientName ?: "Unknown",
            branchId       = branchId,
            userId         = userId,
            adjustmentAmount = adjustmentAmount,
            reason         = reason,
            dateTime       = dateTime,
            lastModified   = lastModified,
            isSynced       = isSynced,
            syncedAt       = syncedAt
        )
    }
}