package fruitylicious.service

import fruitylicious.dto.*
import fruitylicious.repository.*
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ReportService(
    private val branchRepository: BranchRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionItemRepository: TransactionItemRepository,
    private val wasteLogRepository: WasteLogRepository,
    private val restockLogRepository: RestockLogRepository,
    private val inventoryRepository: InventoryRepository,
    private val auditLogRepository: AuditLogRepository,
    private val staffLogRepository: StaffLogRepository
) {

    // ── Sales ─────────────────────────────────────────────────────────────────

    fun getSalesReport(branchId: Long, from: Instant, to: Instant): SalesReportResponse {
        val branch = branchRepository.findById(branchId)
            .orElseThrow { NoSuchElementException("Branch $branchId not found.") }

        val transactions = transactionRepository
            .findAllByBranchBranchIdAndDateTimeBetween(branchId, from, to)
            .filter { it.status == "completed" }

        val totalSales = transactionRepository.sumTotalAmountByBranchAndDateRange(branchId, from, to)

        return SalesReportResponse(
            branchId = branchId,
            branchName = branch.branchName,
            from = from,
            to = to,
            totalSales = totalSales,
            transactionCount = transactions.size
        )
    }

    fun getCombinedSalesReport(from: Instant, to: Instant): CombinedSalesReportResponse {
        val branches = branchRepository.findAll()

        val perBranch = branches.map { branch ->
            val transactions = transactionRepository
                .findAllByBranchBranchIdAndDateTimeBetween(branch.branchId, from, to)
                .filter { it.status == "completed" }
            val total = transactionRepository
                .sumTotalAmountByBranchAndDateRange(branch.branchId, from, to)
            SalesReportResponse(
                branchId = branch.branchId,
                branchName = branch.branchName,
                from = from,
                to = to,
                totalSales = total,
                transactionCount = transactions.size
            )
        }

        val grandTotal = transactionRepository.sumTotalAmountAllBranches(from, to)
        val totalCount = perBranch.sumOf { it.transactionCount }

        return CombinedSalesReportResponse(
            from = from,
            to = to,
            totalSales = grandTotal,
            transactionCount = totalCount,
            perBranch = perBranch
        )
    }

    // ── Waste ─────────────────────────────────────────────────────────────────

    fun getWasteReport(branchId: Long, from: Instant, to: Instant): WasteReportResponse {
        val branch = branchRepository.findById(branchId)
            .orElseThrow { NoSuchElementException("Branch $branchId not found.") }

        val entries = wasteLogRepository
            .findAllByBranchBranchIdAndDateTimeBetween(branchId, from, to)
            .map { it.toResponse() }

        return WasteReportResponse(
            branchId = branchId,
            branchName = branch.branchName,
            from = from,
            to = to,
            entries = entries
        )
    }

    // ── Restock ───────────────────────────────────────────────────────────────

    fun getRestockReport(branchId: Long, from: Instant, to: Instant): RestockReportResponse {
        val branch = branchRepository.findById(branchId)
            .orElseThrow { NoSuchElementException("Branch $branchId not found.") }

        val entries = restockLogRepository
            .findAllByBranchBranchIdAndDateTimeBetween(branchId, from, to)
            .map { it.toResponse() }

        return RestockReportResponse(
            branchId = branchId,
            branchName = branch.branchName,
            from = from,
            to = to,
            entries = entries
        )
    }

    // ── Inventory ─────────────────────────────────────────────────────────────

    fun getInventoryReport(branchId: Long): InventoryReportResponse {
        val branch = branchRepository.findById(branchId)
            .orElseThrow { NoSuchElementException("Branch $branchId not found.") }

        val items = inventoryRepository.findAllByBranchId(branchId)
            .map { it.toResponse() }

        return InventoryReportResponse(
            branchId = branchId,
            branchName = branch.branchName,
            items = items
        )
    }

    // ── Transactions ──────────────────────────────────────────────────────────

    fun getTransactionReport(branchId: Long, from: Instant, to: Instant): TransactionReportResponse {
        val branch = branchRepository.findById(branchId)
            .orElseThrow { NoSuchElementException("Branch $branchId not found.") }

        val transactions = transactionRepository
            .findAllByBranchBranchIdAndDateTimeBetween(branchId, from, to)
            .map { tx ->
                val items = transactionItemRepository
                    .findAllByTransactionTransactionId(tx.transactionId)
                    .map { it.toResponse() }
                tx.toResponse(items)
            }

        return TransactionReportResponse(
            branchId = branchId,
            branchName = branch.branchName,
            from = from,
            to = to,
            transactions = transactions
        )
    }

    // ── Staff Logs ────────────────────────────────────────────────────────────

    fun getStaffLogReport(branchId: Long, from: Instant, to: Instant): StaffLogReportResponse {
        val branch = branchRepository.findById(branchId)
            .orElseThrow { NoSuchElementException("Branch $branchId not found.") }

        val entries = staffLogRepository
            .findAllByBranchBranchIdAndClockInBetween(branchId, from, to)
            .map { it.toResponse() }

        return StaffLogReportResponse(
            branchId = branchId,
            branchName = branch.branchName,
            from = from,
            to = to,
            entries = entries
        )
    }

    // ── Audit Logs ────────────────────────────────────────────────────────────

    fun getAuditLogReport(branchId: Long, from: Instant, to: Instant): AuditLogReportResponse {
        val branch = branchRepository.findById(branchId)
            .orElseThrow { NoSuchElementException("Branch $branchId not found.") }

        val entries = auditLogRepository
            .findAllByBranchBranchIdAndTimestampBetween(branchId, from, to)
            .map { it.toResponse() }

        return AuditLogReportResponse(
            branchId = branchId,
            branchName = branch.branchName,
            from = from,
            to = to,
            entries = entries
        )
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private fun fruitylicious.entity.WasteLogEntity.toResponse() = WasteLogResponse(
        wasteId = wasteId,
        ingredientId = ingredient.ingredientId,
        ingredientName = ingredient.ingredientName,
        branchId = branch.branchId,
        userId = user.userId,
        userName = user.name,
        quantity = quantity,
        image = image,
        reason = reason,
        dateTime = dateTime,
        lastModified = lastModified
    )

    private fun fruitylicious.entity.RestockLogEntity.toResponse() = RestockLogResponse(
        restockId = restockId,
        ingredientId = ingredient.ingredientId,
        ingredientName = ingredient.ingredientName,
        branchId = branch.branchId,
        userId = user.userId,
        userName = user.name,
        quantityAdded = quantityAdded,
        supplier = supplier,
        dateTime = dateTime,
        lastModified = lastModified
    )

    private fun fruitylicious.entity.Inventory.toResponse() = InventoryResponse(
        ingredientId = ingredient.ingredientId,
        ingredientName = ingredient.ingredientName,
        branchId = branch.branchId,
        currentStock = currentStock,
        unitType = ingredient.unitType,
        lowStockThreshold = ingredient.lowStockThreshold,
        lastModified = lastModified
    )

    private fun fruitylicious.entity.TransactionEntity.toResponse(
        items: List<TransactionItemResponse> = emptyList()
    ) = TransactionResponse(
        transactionId = transactionId,
        userId = user.userId,
        userName = user.name,
        branchId = branch.branchId,
        totalAmount = totalAmount,
        paymentType = paymentType,
        dateTime = dateTime,
        status = status,
        items = items,
        lastModified = lastModified
    )

    private fun fruitylicious.entity.TransactionItemEntity.toResponse() = TransactionItemResponse(
        transactionItemId = transactionItemId,
        transactionId = transaction.transactionId,
        productId = product.productId,
        productName = product.productName,
        quantity = quantity,
        subtotal = subtotal,
        lastModified = lastModified
    )

    private fun fruitylicious.entity.StaffLogEntity.toResponse() = StaffLogResponse(
        logId = logId,
        userId = user.userId,
        userName = user.name,
        branchId = branch.branchId,
        image = image,
        clockIn = clockIn,
        clockOut = clockOut,
        lastModified = lastModified
    )

    private fun fruitylicious.entity.AuditLog.toResponse() = AuditLogResponse(
        logId = logId,
        userId = user.userId,
        userName = user.name,
        branchId = branch.branchId,
        action = action,
        tableAffected = tableAffected,
        timestamp = timestamp,
        lastModified = lastModified
    )
}