package fruitylicious.service

import fruitylicious.dto.TransactionItemResponse
import fruitylicious.dto.TransactionRequest
import fruitylicious.dto.TransactionResponse
import fruitylicious.entity.Transaction
import fruitylicious.entity.TransactionItem
import fruitylicious.repository.local.LocalProductRepository
import fruitylicious.repository.local.LocalTransactionItemRepository
import fruitylicious.repository.local.LocalTransactionRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class TransactionService(
    private val localTransactionRepository: LocalTransactionRepository,
    private val localTransactionItemRepository: LocalTransactionItemRepository,
    private val localProductRepository: LocalProductRepository,
    private val inventoryService: InventoryService,
    private val recipeService: RecipeService,
    private val auditService: AuditService
) {

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getAll(branchId: Long): List<TransactionResponse> =
        localTransactionRepository.findAllByBranchId(branchId)
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(transactionId: Long, branchId: Long, isAdmin: Boolean): TransactionResponse {
        val transaction = localTransactionRepository.findById(transactionId)
            .orElseThrow { EntityNotFoundException("Transaction not found: $transactionId") }

        if (!isAdmin && transaction.branchId != branchId) {
            throw EntityNotFoundException("Transaction not found: $transactionId")
        }

        return transaction.toResponse()
    }

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    @Transactional
    fun create(
        request: TransactionRequest,
        userId: Long,
        branchId: Long
    ): TransactionResponse {

        // --- Step 1: Validate all products exist and calculate totals ---
        data class ResolvedItem(
            val productId: Long,
            val productName: String,
            val quantity: Int,
            val unitPrice: BigDecimal,
            val subtotal: BigDecimal
        )

        val resolvedItems = request.items.map { itemRequest ->
            val product = localProductRepository.findById(itemRequest.productId)
                .orElseThrow { EntityNotFoundException("Product not found: ${itemRequest.productId}") }

            val subtotal = product.price.multiply(BigDecimal(itemRequest.quantity))

            ResolvedItem(
                productId   = product.productId,
                productName = product.productName,
                quantity    = itemRequest.quantity,
                unitPrice   = product.price,
                subtotal    = subtotal
            )
        }

        // --- Step 2: Validate sufficient stock for all ingredients ---
        resolvedItems.forEach { resolved ->
            val recipes = recipeService.getRecipesForProduct(resolved.productId)
            recipes.forEach { recipe ->
                val totalRequired = recipe.quantityRequired
                    .multiply(BigDecimal(resolved.quantity))
                inventoryService.validateStock(
                    ingredientId     = recipe.ingredientId,
                    branchId         = branchId,
                    requiredQuantity = totalRequired
                )
            }
        }

        // --- Step 3: Persist transaction header ---
        val totalAmount = resolvedItems.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.subtotal) }

        val transaction = Transaction().apply {
            this.userId      = userId
            this.branchId    = branchId
            this.totalAmount = totalAmount
            this.paymentType = request.paymentType
            this.dateTime    = LocalDateTime.now()
            this.status      = "completed"
            this.lastModified = LocalDateTime.now()
            this.isSynced    = false
        }
        val savedTransaction = localTransactionRepository.save(transaction)

        // --- Step 4: Persist transaction items ---
        resolvedItems.forEach { resolved ->
            val item = TransactionItem().apply {
                transactionId = savedTransaction.transactionId
                productId     = resolved.productId
                quantity      = resolved.quantity
                subtotal      = resolved.subtotal
                lastModified  = LocalDateTime.now()
                isSynced      = false
            }
            localTransactionItemRepository.save(item)
        }

        // --- Step 5: Deduct inventory per recipe ---
        resolvedItems.forEach { resolved ->
            val recipes = recipeService.getRecipesForProduct(resolved.productId)
            recipes.forEach { recipe ->
                val totalRequired = recipe.quantityRequired
                    .multiply(BigDecimal(resolved.quantity))
                inventoryService.deductStock(
                    ingredientId = recipe.ingredientId,
                    branchId     = branchId,
                    quantity     = totalRequired
                )
            }
        }

        // --- Step 6: Audit ---
        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.CREATE_TRANSACTION,
            tableAffected = "transactions",
            details       = "Created transaction (id=${savedTransaction.transactionId}), " +
                    "total=₱$totalAmount, payment=${request.paymentType}, " +
                    "items=${resolvedItems.size}"
        )

        return savedTransaction.toResponse()
    }

    // -------------------------------------------------------------------------
    // Void
    // -------------------------------------------------------------------------

    @Transactional
    fun voidTransaction(
        transactionId: Long,
        userId: Long,
        branchId: Long,
        isAdmin: Boolean
    ): TransactionResponse {
        val transaction = localTransactionRepository.findById(transactionId)
            .orElseThrow { EntityNotFoundException("Transaction not found: $transactionId") }

        if (!isAdmin && transaction.branchId != branchId) {
            throw EntityNotFoundException("Transaction not found: $transactionId")
        }

        if (transaction.status == "void") {
            throw IllegalArgumentException("Transaction $transactionId is already voided")
        }

        transaction.apply {
            status       = "void"
            lastModified = LocalDateTime.now()
            isSynced     = false
        }
        val saved = localTransactionRepository.save(transaction)

        // Note: Voiding does NOT restore inventory — per business rules

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.VOID_TRANSACTION,
            tableAffected = "transactions",
            details       = "Voided transaction (id=$transactionId), " +
                    "original total=₱${transaction.totalAmount}"
        )

        return saved.toResponse()
    }

    // -------------------------------------------------------------------------
    // Mapper
    // -------------------------------------------------------------------------

    private fun Transaction.toResponse(): TransactionResponse {
        val items = localTransactionItemRepository
            .findAllByTransactionId(transactionId)
            .map { it.toItemResponse() }

        return TransactionResponse(
            transactionId = transactionId,
            userId        = userId,
            branchId      = branchId,
            totalAmount   = totalAmount,
            paymentType   = paymentType,
            dateTime      = dateTime,
            status        = status,
            items         = items,
            lastModified  = lastModified,
            isSynced      = isSynced,
            syncedAt      = syncedAt
        )
    }

    private fun TransactionItem.toItemResponse(): TransactionItemResponse {
        val product = localProductRepository.findById(productId).orElse(null)
        return TransactionItemResponse(
            transactionItemId = transactionItemId,
            transactionId     = transactionId,
            productId         = productId,
            productName       = product?.productName ?: "Unknown",
            quantity          = quantity,
            subtotal          = subtotal,
            lastModified      = lastModified,
            isSynced          = isSynced,
            syncedAt          = syncedAt
        )
    }
}