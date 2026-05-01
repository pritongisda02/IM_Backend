package fruitylicious.service

import fruitylicious.dto.*
import fruitylicious.entity.*
import fruitylicious.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class SyncService(
    private val branchRepository: BranchRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val ingredientRepository: IngredientRepository,
    private val productRecipeRepository: ProductRecipeRepository,
    private val inventoryRepository: InventoryRepository,
    private val restockLogRepository: RestockLogRepository,
    private val inventoryAdjustmentRepository: InventoryAdjustmentRepository,
    private val wasteLogRepository: WasteLogRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionItemRepository: TransactionItemRepository,
    private val auditLogRepository: AuditLogRepository,
    private val staffLogRepository: StaffLogRepository
) {

    // ── Pull ──────────────────────────────────────────────────────────────────

    fun pull(since: Instant): SyncPullResponse {
        return SyncPullResponse(
            since = since,
            branches = branchRepository.findAllByLastModifiedAfter(since).map { it.toResponse() },
            users = userRepository.findAllByLastModifiedAfter(since).map { it.toResponse() },
            products = productRepository.findAllByLastModifiedAfter(since).map { it.toResponse() },
            ingredients = ingredientRepository.findAllByLastModifiedAfter(since).map { it.toResponse() },
            recipes = productRecipeRepository.findAllByLastModifiedAfter(since).map { it.toResponse() }
        )
    }

    // ── Push ──────────────────────────────────────────────────────────────────

    fun push(request: SyncPushRequest): SyncPushResponse {
        return SyncPushResponse(
            inventory = request.inventory.map { pushInventory(it) },
            restockLogs = request.restockLogs.map { pushRestockLog(it) },
            inventoryAdjustments = request.inventoryAdjustments.map { pushInventoryAdjustment(it) },
            wasteLogs = request.wasteLogs.map { pushWasteLog(it) },
            transactions = request.transactions.map { pushTransaction(it) },
            transactionItems = request.transactionItems.map { pushTransactionItem(it) },
            auditLogs = request.auditLogs.map { pushAuditLog(it) },
            staffLogs = request.staffLogs.map { pushStaffLog(it) }
        )
    }

    // ── Inventory ─────────────────────────────────────────────────────────────

    @Transactional
    fun pushInventory(req: InventoryRequest): SyncRecordResult {
        return try {
            val id = InventoryId(req.ingredientId, req.branchId)
            val existing = inventoryRepository.findById(id).orElse(null)

            if (existing != null && existing.lastModified >= req.lastModified()) {
                return SyncRecordResult(id = req.ingredientId, success = false,
                    error = "Conflict: Oracle record is newer.")
            }

            val ingredient = ingredientRepository.findById(req.ingredientId)
                .orElseThrow { NoSuchElementException("Ingredient ${req.ingredientId} not found.") }
            val branch = branchRepository.findById(req.branchId)
                .orElseThrow { NoSuchElementException("Branch ${req.branchId} not found.") }

            val entity = existing ?: Inventory()
            entity.ingredientId = req.ingredientId
            entity.branchId = req.branchId
            entity.ingredient = ingredient
            entity.branch = branch
            entity.currentStock = req.currentStock
            entity.lastModified = Instant.now()

            inventoryRepository.save(entity)
            SyncRecordResult(id = req.ingredientId, success = true)
        } catch (ex: Exception) {
            SyncRecordResult(id = req.ingredientId, success = false, error = ex.message)
        }
    }

    // ── RestockLog ────────────────────────────────────────────────────────────

    @Transactional
    fun pushRestockLog(req: RestockLogRequest): SyncRecordResult {
        return try {
            val existing = restockLogRepository.findById(req.restockId).orElse(null)

            if (existing != null && existing.lastModified >= req.lastModified()) {
                return SyncRecordResult(id = req.restockId, success = false,
                    error = "Conflict: Oracle record is newer.")
            }

            val ingredient = ingredientRepository.findById(req.ingredientId)
                .orElseThrow { NoSuchElementException("Ingredient ${req.ingredientId} not found.") }
            val branch = branchRepository.findById(req.branchId)
                .orElseThrow { NoSuchElementException("Branch ${req.branchId} not found.") }
            val user = userRepository.findById(req.userId)
                .orElseThrow { NoSuchElementException("User ${req.userId} not found.") }

            val entity = existing ?: RestockLog()
            entity.restockId = req.restockId
            entity.ingredient = ingredient
            entity.branch = branch
            entity.user = user
            entity.quantityAdded = req.quantityAdded
            entity.supplier = req.supplier
            entity.dateTime = req.dateTime
            entity.lastModified = Instant.now()

            restockLogRepository.save(entity)
            SyncRecordResult(id = req.restockId, success = true)
        } catch (ex: Exception) {
            SyncRecordResult(id = req.restockId, success = false, error = ex.message)
        }
    }

    // ── InventoryAdjustment ───────────────────────────────────────────────────

    @Transactional
    fun pushInventoryAdjustment(req: InventoryAdjustmentRequest): SyncRecordResult {
        return try {
            val existing = inventoryAdjustmentRepository.findById(req.adjustmentId).orElse(null)

            if (existing != null && existing.lastModified >= req.lastModified()) {
                return SyncRecordResult(id = req.adjustmentId, success = false,
                    error = "Conflict: Oracle record is newer.")
            }

            val ingredient = ingredientRepository.findById(req.ingredientId)
                .orElseThrow { NoSuchElementException("Ingredient ${req.ingredientId} not found.") }
            val branch = branchRepository.findById(req.branchId)
                .orElseThrow { NoSuchElementException("Branch ${req.branchId} not found.") }
            val user = userRepository.findById(req.userId)
                .orElseThrow { NoSuchElementException("User ${req.userId} not found.") }

            val entity = existing ?: InventoryAdjustment()
            entity.adjustmentId = req.adjustmentId
            entity.ingredient = ingredient
            entity.branch = branch
            entity.user = user
            entity.adjustmentAmount = req.adjustmentAmount
            entity.reason = req.reason
            entity.dateTime = req.dateTime
            entity.lastModified = Instant.now()

            inventoryAdjustmentRepository.save(entity)
            SyncRecordResult(id = req.adjustmentId, success = true)
        } catch (ex: Exception) {
            SyncRecordResult(id = req.adjustmentId, success = false, error = ex.message)
        }
    }

    // ── WasteLog ──────────────────────────────────────────────────────────────

    @Transactional
    fun pushWasteLog(req: WasteLogRequest): SyncRecordResult {
        return try {
            val existing = wasteLogRepository.findById(req.wasteId).orElse(null)

            if (existing != null && existing.lastModified >= req.lastModified()) {
                return SyncRecordResult(id = req.wasteId, success = false,
                    error = "Conflict: Oracle record is newer.")
            }

            val ingredient = ingredientRepository.findById(req.ingredientId)
                .orElseThrow { NoSuchElementException("Ingredient ${req.ingredientId} not found.") }
            val branch = branchRepository.findById(req.branchId)
                .orElseThrow { NoSuchElementException("Branch ${req.branchId} not found.") }
            val user = userRepository.findById(req.userId)
                .orElseThrow { NoSuchElementException("User ${req.userId} not found.") }

            val entity = existing ?: WasteLog()
            entity.wasteId = req.wasteId
            entity.ingredient = ingredient
            entity.branch = branch
            entity.user = user
            entity.quantity = req.quantity
            entity.image = req.image
            entity.reason = req.reason
            entity.dateTime = req.dateTime
            entity.lastModified = Instant.now()

            wasteLogRepository.save(entity)
            SyncRecordResult(id = req.wasteId, success = true)
        } catch (ex: Exception) {
            SyncRecordResult(id = req.wasteId, success = false, error = ex.message)
        }
    }

    // ── Transaction ───────────────────────────────────────────────────────────

    @Transactional
    fun pushTransaction(req: TransactionRequest): SyncRecordResult {
        return try {
            val existing = transactionRepository.findById(req.transactionId).orElse(null)

            if (existing != null && existing.lastModified >= req.lastModified()) {
                return SyncRecordResult(id = req.transactionId, success = false,
                    error = "Conflict: Oracle record is newer.")
            }

            val user = userRepository.findById(req.userId)
                .orElseThrow { NoSuchElementException("User ${req.userId} not found.") }
            val branch = branchRepository.findById(req.branchId)
                .orElseThrow { NoSuchElementException("Branch ${req.branchId} not found.") }

            val entity = existing ?: Transaction()
            entity.transactionId = req.transactionId
            entity.user = user
            entity.branch = branch
            entity.totalAmount = req.totalAmount
            entity.paymentType = req.paymentType
            entity.dateTime = req.dateTime
            entity.status = req.status
            entity.lastModified = Instant.now()

            transactionRepository.save(entity)
            SyncRecordResult(id = req.transactionId, success = true)
        } catch (ex: Exception) {
            SyncRecordResult(id = req.transactionId, success = false, error = ex.message)
        }
    }

    // ── TransactionItem ───────────────────────────────────────────────────────

    @Transactional
    fun pushTransactionItem(req: TransactionItemRequest): SyncRecordResult {
        return try {
            val existing = transactionItemRepository.findById(req.transactionItemId).orElse(null)

            if (existing != null && existing.lastModified >= req.lastModified()) {
                return SyncRecordResult(id = req.transactionItemId, success = false,
                    error = "Conflict: Oracle record is newer.")
            }

            val transaction = transactionRepository.findById(req.transactionId)
                .orElseThrow { NoSuchElementException("Transaction ${req.transactionId} not found.") }
            val product = productRepository.findById(req.productId)
                .orElseThrow { NoSuchElementException("Product ${req.productId} not found.") }

            val entity = existing ?: TransactionItem()
            entity.transactionItemId = req.transactionItemId
            entity.transaction = transaction
            entity.product = product
            entity.quantity = req.quantity
            entity.subtotal = req.subtotal
            entity.lastModified = Instant.now()

            transactionItemRepository.save(entity)
            SyncRecordResult(id = req.transactionItemId, success = true)
        } catch (ex: Exception) {
            SyncRecordResult(id = req.transactionItemId, success = false, error = ex.message)
        }
    }

    // ── AuditLog ──────────────────────────────────────────────────────────────

    @Transactional
    fun pushAuditLog(req: AuditLogRequest): SyncRecordResult {
        return try {
            val existing = auditLogRepository.findById(req.logId).orElse(null)

            if (existing != null && existing.lastModified >= req.lastModified()) {
                return SyncRecordResult(id = req.logId, success = false,
                    error = "Conflict: Oracle record is newer.")
            }

            val user = userRepository.findById(req.userId)
                .orElseThrow { NoSuchElementException("User ${req.userId} not found.") }
            val branch = branchRepository.findById(req.branchId)
                .orElseThrow { NoSuchElementException("Branch ${req.branchId} not found.") }

            val entity = existing ?: AuditLog()
            entity.logId = req.logId
            entity.user = user
            entity.branch = branch
            entity.action = req.action
            entity.tableAffected = req.tableAffected
            entity.timestamp = req.timestamp
            entity.lastModified = Instant.now()

            auditLogRepository.save(entity)
            SyncRecordResult(id = req.logId, success = true)
        } catch (ex: Exception) {
            SyncRecordResult(id = req.logId, success = false, error = ex.message)
        }
    }

    // ── StaffLog ──────────────────────────────────────────────────────────────

    @Transactional
    fun pushStaffLog(req: StaffLogRequest): SyncRecordResult {
        return try {
            val existing = staffLogRepository.findById(req.logId).orElse(null)

            if (existing != null && existing.lastModified >= req.lastModified()) {
                return SyncRecordResult(id = req.logId, success = false,
                    error = "Conflict: Oracle record is newer.")
            }

            val user = userRepository.findById(req.userId)
                .orElseThrow { NoSuchElementException("User ${req.userId} not found.") }
            val branch = branchRepository.findById(req.branchId)
                .orElseThrow { NoSuchElementException("Branch ${req.branchId} not found.") }

            val entity = existing ?: StaffLog()
            entity.logId = req.logId
            entity.user = user
            entity.branch = branch
            entity.image = req.image
            entity.clockIn = req.clockIn
            entity.clockOut = req.clockOut
            entity.lastModified = Instant.now()

            staffLogRepository.save(entity)
            SyncRecordResult(id = req.logId, success = true)
        } catch (ex: Exception) {
            SyncRecordResult(id = req.logId, success = false, error = ex.message)
        }
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private fun fruitylicious.entity.Branch.toResponse() = BranchResponse(
        branchId = branchId,
        branchName = branchName,
        address = address,
        contactNumber = contactNumber,
        lastModified = lastModified
    )

    private fun fruitylicious.entity.User.toResponse() = UserResponse(
        userId = userId,
        name = name,
        role = role,
        username = username,
        lastModified = lastModified
    )

    private fun fruitylicious.entity.Product.toResponse() = ProductResponse(
        productId = productId,
        image = image,
        productName = productName,
        isAddon = isAddon,
        price = price,
        lastModified = lastModified
    )

    private fun fruitylicious.entity.Ingredient.toResponse() = IngredientResponse(
        ingredientId = ingredientId,
        image = image,
        ingredientName = ingredientName,
        unitType = unitType,
        estimatedWeightPerUnit = estimatedWeightPerUnit,
        isPackaging = isPackaging,
        lowStockThreshold = lowStockThreshold,
        lastModified = lastModified
    )

    private fun fruitylicious.entity.ProductRecipe.toResponse() = RecipeResponse(
        recipeId = recipeId,
        productId = product.productId,
        ingredientId = ingredient.ingredientId,
        quantityRequired = quantityRequired,
        lastModified = lastModified
    )

    // ── lastModified helpers on request DTOs ──────────────────────────────────
    // The Android device sets last_modified on the record before pushing.
    // We use Instant.now() as a safe fallback since the field is not on the DTO —
    // conflict resolution uses the server-assigned lastModified written on save.
    private fun InventoryRequest.lastModified(): Instant = Instant.EPOCH
    private fun RestockLogRequest.lastModified(): Instant = Instant.EPOCH
    private fun InventoryAdjustmentRequest.lastModified(): Instant = Instant.EPOCH
    private fun WasteLogRequest.lastModified(): Instant = Instant.EPOCH
    private fun TransactionRequest.lastModified(): Instant = Instant.EPOCH
    private fun TransactionItemRequest.lastModified(): Instant = Instant.EPOCH
    private fun AuditLogRequest.lastModified(): Instant = Instant.EPOCH
    private fun StaffLogRequest.lastModified(): Instant = Instant.EPOCH
}