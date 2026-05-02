package fruitylicious.sync

import fruitylicious.entity.InventoryId
import fruitylicious.entity.TransactionEntity
import fruitylicious.entity.TransactionItemEntity
import fruitylicious.repository.AuditLogRepository
import fruitylicious.repository.BranchRepository
import fruitylicious.repository.IngredientRepository
import fruitylicious.repository.InventoryAdjustmentRepository
import fruitylicious.repository.InventoryRepository
import fruitylicious.repository.ProductRecipeRepository
import fruitylicious.repository.ProductRepository
import fruitylicious.repository.ProductVariantRepository
import fruitylicious.repository.RestockLogRepository
import fruitylicious.repository.StaffLogRepository
import fruitylicious.repository.TransactionItemAddonRepository
import fruitylicious.repository.TransactionItemRepository
import fruitylicious.repository.TransactionRepository
import fruitylicious.repository.UserRepository
import fruitylicious.repository.WasteLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SyncService(
    private val branchRepository: BranchRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val productVariantRepository: ProductVariantRepository,
    private val ingredientRepository: IngredientRepository,
    private val productRecipeRepository: ProductRecipeRepository,
    private val inventoryRepository: InventoryRepository,
    private val restockLogRepository: RestockLogRepository,
    private val inventoryAdjustmentRepository: InventoryAdjustmentRepository,
    private val wasteLogRepository: WasteLogRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionItemRepository: TransactionItemRepository,
    private val transactionItemAddonRepository: TransactionItemAddonRepository,
    private val staffLogRepository: StaffLogRepository,
    private val auditLogRepository: AuditLogRepository
) {

    @Transactional
    fun push(
        request: PushRequest,
        requestingBranchId: Int
    ): PushResponse {
        val serverTime = System.currentTimeMillis()

        println("========== SYNC PUSH START ==========")
        println("SYNC PUSH requestingBranchId=$requestingBranchId")
        println("SYNC PUSH inventory count=${request.inventory.size}")
        println("SYNC PUSH restockLogs count=${request.restockLogs.size}")

        request.inventory.forEach {
            println(
                "SYNC PUSH inventory item ingredientId=${it.ingredientId}, branchId=${it.branchId}, stock=${it.currentStock}, lastModified=${it.lastModified}"
            )
        }

        request.restockLogs.forEach {
            println(
                "SYNC PUSH restock item restockId=${it.restockId}, ingredientId=${it.ingredientId}, branchId=${it.branchId}, qty=${it.quantityAdded}"
            )
        }
        println("========== SYNC PUSH END HEADER ==========")

        val incomingTransactionsById = request.transactions.associateBy {
            it.transactionId
        }

        val incomingTransactionItemsById = request.transactionItems.associateBy {
            it.transactionItemId
        }

        return PushResponse(
            branches = request.branches.map { item ->
                syncRecord(
                    recordId = item.branchId.toString(),
                    incoming = item.copyForServer(serverTime),
                    current = { branchRepository.findById(item.branchId).orElse(null) },
                    save = { branchRepository.save(it) },
                    lastModified = { it.lastModified }
                )
            },

            users = request.users.map { item ->
                syncRecord(
                    recordId = item.userId.toString(),
                    incoming = item.copyForServer(serverTime),
                    current = { userRepository.findById(item.userId).orElse(null) },
                    save = { userRepository.save(it) },
                    lastModified = { it.lastModified }
                )
            },

            products = request.products.map { item ->
                syncRecord(
                    recordId = item.productId.toString(),
                    incoming = item.copyForServer(serverTime),
                    current = { productRepository.findById(item.productId).orElse(null) },
                    save = { productRepository.save(it) },
                    lastModified = { it.lastModified }
                )
            },

            productVariants = request.productVariants.map { item ->
                syncRecord(
                    recordId = item.variantId.toString(),
                    incoming = item.copyForServer(serverTime),
                    current = { productVariantRepository.findById(item.variantId).orElse(null) },
                    save = { productVariantRepository.save(it) },
                    lastModified = { it.lastModified }
                )
            },

            ingredients = request.ingredients.map { item ->
                syncRecord(
                    recordId = item.ingredientId.toString(),
                    incoming = item.copyForServer(serverTime),
                    current = { ingredientRepository.findById(item.ingredientId).orElse(null) },
                    save = { ingredientRepository.save(it) },
                    lastModified = { it.lastModified }
                )
            },

            productRecipes = request.productRecipes.map { item ->
                syncRecord(
                    recordId = item.recipeId.toString(),
                    incoming = item.copyForServer(serverTime),
                    current = { productRecipeRepository.findById(item.recipeId).orElse(null) },
                    save = { productRecipeRepository.save(it) },
                    lastModified = { it.lastModified }
                )
            },

            inventory = request.inventory.map { item ->
                val recordId = "${item.ingredientId}:${item.branchId}"

                if (item.branchId != requestingBranchId) {
                    branchRejected(recordId)
                } else {
                    val id = InventoryId(
                        ingredientId = item.ingredientId,
                        branchId = item.branchId
                    )

                    syncRecord(
                        recordId = recordId,
                        incoming = item.copyForServer(serverTime),
                        current = { inventoryRepository.findById(id).orElse(null) },
                        save = { inventoryRepository.save(it) },
                        lastModified = { it.lastModified }
                    )
                }
            },

            restockLogs = request.restockLogs.map { item ->
                if (item.branchId != requestingBranchId) {
                    branchRejected(item.restockId)
                } else {
                    syncRecord(
                        recordId = item.restockId,
                        incoming = item.copyForServer(serverTime),
                        current = { restockLogRepository.findById(item.restockId).orElse(null) },
                        save = { restockLogRepository.save(it) },
                        lastModified = { it.lastModified }
                    )
                }
            },

            inventoryAdjustments = request.inventoryAdjustments.map { item ->
                if (item.branchId != requestingBranchId) {
                    branchRejected(item.adjustmentId)
                } else {
                    syncRecord(
                        recordId = item.adjustmentId,
                        incoming = item.copyForServer(serverTime),
                        current = { inventoryAdjustmentRepository.findById(item.adjustmentId).orElse(null) },
                        save = { inventoryAdjustmentRepository.save(it) },
                        lastModified = { it.lastModified }
                    )
                }
            },

            wasteLogs = request.wasteLogs.map { item ->
                if (item.branchId != requestingBranchId) {
                    branchRejected(item.wasteId)
                } else {
                    syncRecord(
                        recordId = item.wasteId,
                        incoming = item.copyForServer(serverTime),
                        current = { wasteLogRepository.findById(item.wasteId).orElse(null) },
                        save = { wasteLogRepository.save(it) },
                        lastModified = { it.lastModified }
                    )
                }
            },

            transactions = request.transactions.map { item ->
                if (item.branchId != requestingBranchId) {
                    branchRejected(item.transactionId)
                } else {
                    syncRecord(
                        recordId = item.transactionId,
                        incoming = item.copyForServer(serverTime),
                        current = { transactionRepository.findById(item.transactionId).orElse(null) },
                        save = { transactionRepository.save(it) },
                        lastModified = { it.lastModified }
                    )
                }
            },

            transactionItems = request.transactionItems.map { item ->
                val branchId = resolveTransactionBranchId(
                    transactionId = item.transactionId,
                    incomingTransactionsById = incomingTransactionsById
                )

                when {
                    branchId == null -> parentMissing(item.transactionItemId)

                    branchId != requestingBranchId -> branchRejected(item.transactionItemId)

                    else -> syncRecord(
                        recordId = item.transactionItemId,
                        incoming = item.copyForServer(serverTime),
                        current = { transactionItemRepository.findById(item.transactionItemId).orElse(null) },
                        save = { transactionItemRepository.save(it) },
                        lastModified = { it.lastModified }
                    )
                }
            },

            transactionItemAddons = request.transactionItemAddons.map { item ->
                val parentItem = incomingTransactionItemsById[item.transactionItemId]
                    ?: transactionItemRepository.findById(item.transactionItemId).orElse(null)

                val branchId = parentItem?.let {
                    resolveTransactionBranchId(
                        transactionId = it.transactionId,
                        incomingTransactionsById = incomingTransactionsById
                    )
                }

                when {
                    parentItem == null -> parentMissing(item.transactionItemAddonId)

                    branchId == null -> parentMissing(item.transactionItemAddonId)

                    branchId != requestingBranchId -> branchRejected(item.transactionItemAddonId)

                    else -> syncRecord(
                        recordId = item.transactionItemAddonId,
                        incoming = item.copyForServer(serverTime),
                        current = { transactionItemAddonRepository.findById(item.transactionItemAddonId).orElse(null) },
                        save = { transactionItemAddonRepository.save(it) },
                        lastModified = { it.lastModified }
                    )
                }
            },

            staffLogs = request.staffLogs.map { item ->
                if (item.branchId != requestingBranchId) {
                    branchRejected(item.logId)
                } else {
                    syncRecord(
                        recordId = item.logId,
                        incoming = item.copyForServer(serverTime),
                        current = { staffLogRepository.findById(item.logId).orElse(null) },
                        save = { staffLogRepository.save(it) },
                        lastModified = { it.lastModified }
                    )
                }
            },

            auditLogs = request.auditLogs.map { item ->
                if (item.branchId != requestingBranchId) {
                    branchRejected(item.logId)
                } else {
                    syncRecord(
                        recordId = item.logId,
                        incoming = item.copyForServer(serverTime),
                        current = { auditLogRepository.findById(item.logId).orElse(null) },
                        save = { auditLogRepository.save(it) },
                        lastModified = { it.lastModified }
                    )
                }
            }
        )
    }

    fun pull(
        requestingBranchId: Int,
        since: Long
    ): PullResponse {
        val serverTime = System.currentTimeMillis()

        return PullResponse(
            since = since,
            serverTime = serverTime,

            branches = branchRepository.findByLastModifiedGreaterThan(since),
            users = userRepository.findByLastModifiedGreaterThan(since),
            products = productRepository.findByLastModifiedGreaterThan(since),
            productVariants = productVariantRepository.findByLastModifiedGreaterThan(since),
            ingredients = ingredientRepository.findByLastModifiedGreaterThan(since),
            productRecipes = productRecipeRepository.findByLastModifiedGreaterThan(since),

            inventory = inventoryRepository.findByIdBranchIdAndLastModifiedGreaterThan(
                branchId = requestingBranchId,
                lastModified = since
            ),
            restockLogs = restockLogRepository.findByBranchIdAndLastModifiedGreaterThan(
                branchId = requestingBranchId,
                lastModified = since
            ),
            inventoryAdjustments = inventoryAdjustmentRepository.findByBranchIdAndLastModifiedGreaterThan(
                branchId = requestingBranchId,
                lastModified = since
            ),
            wasteLogs = wasteLogRepository.findByBranchIdAndLastModifiedGreaterThan(
                branchId = requestingBranchId,
                lastModified = since
            ),
            transactions = transactionRepository.findByBranchIdAndLastModifiedGreaterThan(
                branchId = requestingBranchId,
                lastModified = since
            ),
            transactionItems = transactionItemRepository.findChangedByBranchSince(
                branchId = requestingBranchId,
                since = since
            ),
            transactionItemAddons = transactionItemAddonRepository.findChangedByBranchSince(
                branchId = requestingBranchId,
                since = since
            ),
            staffLogs = staffLogRepository.findByBranchIdAndLastModifiedGreaterThan(
                branchId = requestingBranchId,
                lastModified = since
            ),
            auditLogs = auditLogRepository.findByBranchIdAndLastModifiedGreaterThan(
                branchId = requestingBranchId,
                lastModified = since
            )
        )
    }

    private fun <T> syncRecord(
        recordId: String,
        incoming: T,
        current: () -> T?,
        save: (T) -> T,
        lastModified: (T) -> Long
    ): SyncRecordResult {
        return try {
            val existing = current()

            if (existing == null || lastModified(incoming) >= lastModified(existing)) {
                save(incoming)
            }

            SyncRecordResult(
                recordId = recordId,
                success = true
            )
        } catch (exception: Exception) {
            SyncRecordResult(
                recordId = recordId,
                success = false,
                error = exception.message ?: "Sync failed."
            )
        }
    }

    private fun branchRejected(recordId: String): SyncRecordResult {
        return SyncRecordResult(
            recordId = recordId,
            success = false,
            error = "Record branch does not match requesting branch."
        )
    }

    private fun parentMissing(recordId: String): SyncRecordResult {
        return SyncRecordResult(
            recordId = recordId,
            success = false,
            error = "Parent record was not found."
        )
    }

    private fun resolveTransactionBranchId(
        transactionId: String,
        incomingTransactionsById: Map<String, TransactionEntity>
    ): Int? {
        return incomingTransactionsById[transactionId]?.branchId
            ?: transactionRepository.findById(transactionId).orElse(null)?.branchId
    }
}