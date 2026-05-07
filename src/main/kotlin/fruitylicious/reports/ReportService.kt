package fruitylicious.reports

import fruitylicious.repository.AuditLogRepository
import fruitylicious.repository.BranchRepository
import fruitylicious.repository.InventoryAdjustmentRepository
import fruitylicious.repository.InventoryRepository
import fruitylicious.repository.RestockLogRepository
import fruitylicious.repository.StaffLogRepository
import fruitylicious.repository.TransactionItemRepository
import fruitylicious.repository.TransactionRepository
import fruitylicious.repository.WasteLogRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ReportsService(
    private val branchRepository: BranchRepository,
    private val inventoryRepository: InventoryRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionItemRepository: TransactionItemRepository,
    private val wasteLogRepository: WasteLogRepository,
    private val restockLogRepository: RestockLogRepository,
    private val inventoryAdjustmentRepository: InventoryAdjustmentRepository,
    private val staffLogRepository: StaffLogRepository,
    private val auditLogRepository: AuditLogRepository
) {

    fun salesReport(
        branchId: Int?,
        from: Long,
        to: Long
    ): SalesReportDto {
        val summary = salesSummary(branchId, from, to)

        val items = transactionItemRepository.getSalesItems(
            branchId = branchId,
            from = from,
            to = to
        ).map {
            SalesReportItemDto(
                productId = it.productId,
                productName = it.productName,
                quantitySold = it.quantitySold.toInt(),
                grossSales = it.grossSales
            )
        }

        return SalesReportDto(
            branchId = branchId,
            branchName = summary.branchName,
            from = from,
            to = to,
            totalSales = summary.totalSales,
            totalTransactions = summary.totalTransactions,
            averageTransactionValue = summary.averageTransactionValue,
            previousSales = summary.previousSales,
            cashTotal = summary.cashTotal,
            gcashTotal = summary.gcashTotal,
            items = items
        )
    }

    fun salesSummary(
        branchId: Int?,
        from: Long,
        to: Long
    ): SalesSummaryDto {
        val branchName = branchId?.let {
            branchRepository.findById(it).orElse(null)?.branchName
        }

        val summary = transactionRepository.getSalesSummary(
            branchId = branchId,
            from = from,
            to = to
        )

        val totalSales = summary.totalSales ?: 0.0
        val totalTransactions = summary.totalTransactions?.toInt() ?: 0

        val paymentBreakdown = transactionRepository.getPaymentBreakdown(
            branchId = branchId,
            from = from,
            to = to
        )

        val cashTotal = paymentBreakdown
            .firstOrNull { it.paymentType.equals("Cash", ignoreCase = true) }
            ?.totalAmount ?: 0.0

        val gcashTotal = paymentBreakdown
            .firstOrNull { it.paymentType.equals("Gcash", ignoreCase = true) }
            ?.totalAmount ?: 0.0

        val periodLength = (to - from).coerceAtLeast(0L)
        val previousFrom = from - periodLength
        val previousTo = from

        val previousSummary = transactionRepository.getSalesSummary(
            branchId = branchId,
            from = previousFrom,
            to = previousTo
        )

        return SalesSummaryDto(
            branchId = branchId,
            branchName = branchName ?: branchId?.let { "Branch $it" } ?: "All Branches",
            from = from,
            to = to,
            totalSales = totalSales,
            totalTransactions = totalTransactions,
            averageTransactionValue = if (totalTransactions > 0) {
                totalSales / totalTransactions
            } else {
                0.0
            },
            previousSales = previousSummary.totalSales ?: 0.0,
            cashTotal = cashTotal,
            gcashTotal = gcashTotal
        )
    }

    fun salesItemsPage(
        branchId: Int?,
        from: Long,
        to: Long,
        page: Int,
        size: Int
    ): PageResponseDto<SalesReportItemDto> {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, 100)
        )

        val result = transactionItemRepository.getSalesItemsPage(
            branchId = branchId,
            from = from,
            to = to,
            pageable = pageable
        ).map {
            SalesReportItemDto(
                productId = it.productId,
                productName = it.productName,
                quantitySold = it.quantitySold.toInt(),
                grossSales = it.grossSales
            )
        }

        return PageResponseDto.fromPage(result)
    }

    fun topSellingItems(
        branchId: Int?,
        from: Long,
        to: Long,
        limit: Int
    ): List<SalesReportItemDto> {
        val pageable = PageRequest.of(
            0,
            limit.coerceIn(1, 20)
        )

        return transactionItemRepository.getSalesItemsPage(
            branchId = branchId,
            from = from,
            to = to,
            pageable = pageable
        ).content.map {
            SalesReportItemDto(
                productId = it.productId,
                productName = it.productName,
                quantitySold = it.quantitySold.toInt(),
                grossSales = it.grossSales
            )
        }
    }

    fun inventoryReport(
        branchId: Int
    ): InventoryReportDto {
        val branch = branchRepository.findById(branchId).orElse(null)

        val items = inventoryRepository.getInventoryReportRows(branchId).map {
            InventoryReportItemDto(
                ingredientId = it.ingredientId,
                ingredientName = it.ingredientName,
                unitType = it.unitType,
                currentStock = it.currentStock,
                lowStockThreshold = it.lowStockThreshold,
                isLowStock = it.currentStock <= it.lowStockThreshold
            )
        }

        return InventoryReportDto(
            branchId = branchId,
            branchName = branch?.branchName ?: "Branch $branchId",
            generatedAt = System.currentTimeMillis(),
            items = items
        )
    }

    fun wasteReport(
        branchId: Int,
        from: Long,
        to: Long
    ): WasteReportDto {
        val branch = branchRepository.findById(branchId).orElse(null)

        val items = wasteLogRepository.getWasteReportRows(
            branchId = branchId,
            from = from,
            to = to
        ).map {
            WasteReportItemDto(
                wasteId = it.wasteId,
                ingredientId = it.ingredientId,
                ingredientName = it.ingredientName,
                quantity = it.quantity,
                unitType = it.unitType,
                reason = it.reason,
                userId = it.userId,
                userName = it.userName,
                dateTime = it.dateTime,
                image = it.image
            )
        }

        return WasteReportDto(
            branchId = branchId,
            branchName = branch?.branchName ?: "Branch $branchId",
            from = from,
            to = to,
            totalWasteQuantity = items.sumOf { it.quantity },
            items = items
        )
    }

    fun wasteSummary(
        branchId: Int?,
        from: Long,
        to: Long
    ): WasteSummaryDto {
        val summary = wasteLogRepository.getWasteSummary(
            branchId = branchId,
            from = from,
            to = to
        )

        return WasteSummaryDto(
            branchId = branchId,
            branchName = branchName(branchId),
            from = from,
            to = to,
            totalWasteQuantity = summary.totalWasteQuantity ?: 0.0,
            totalWasteEntries = summary.totalWasteEntries ?: 0L
        )
    }

    fun wastePage(
        branchId: Int?,
        from: Long,
        to: Long,
        page: Int,
        size: Int
    ): PageResponseDto<WasteReportItemDto> {
        val pageable = pageRequest(page, size)

        val result = wasteLogRepository.getWasteReportRowsPage(
            branchId = branchId,
            from = from,
            to = to,
            pageable = pageable
        ).map {
            WasteReportItemDto(
                wasteId = it.wasteId,
                ingredientId = it.ingredientId,
                ingredientName = it.ingredientName,
                quantity = it.quantity,
                unitType = it.unitType,
                reason = it.reason,
                userId = it.userId,
                userName = it.userName,
                dateTime = it.dateTime,
                image = null
            )
        }

        return PageResponseDto.fromPage(result)
    }

    fun restockReport(
        branchId: Int,
        from: Long,
        to: Long
    ): RestockReportDto {
        val branch = branchRepository.findById(branchId).orElse(null)

        val items = restockLogRepository.getRestockReportRows(
            branchId = branchId,
            from = from,
            to = to
        ).map {
            RestockReportItemDto(
                restockId = it.restockId,
                ingredientId = it.ingredientId,
                ingredientName = it.ingredientName,
                quantityAdded = it.quantityAdded,
                unitType = it.unitType,
                supplier = it.supplier,
                userId = it.userId,
                userName = it.userName,
                dateTime = it.dateTime
            )
        }

        return RestockReportDto(
            branchId = branchId,
            branchName = branch?.branchName ?: "Branch $branchId",
            from = from,
            to = to,
            totalRestockQuantity = items.sumOf { it.quantityAdded },
            items = items
        )
    }

    fun restockSummary(
        branchId: Int?,
        from: Long,
        to: Long
    ): RestockSummaryDto {
        val summary = restockLogRepository.getRestockSummary(
            branchId = branchId,
            from = from,
            to = to
        )

        return RestockSummaryDto(
            branchId = branchId,
            branchName = branchName(branchId),
            from = from,
            to = to,
            totalRestockQuantity = summary.totalRestockQuantity ?: 0.0,
            totalRestockEntries = summary.totalRestockEntries ?: 0L
        )
    }

    fun restockPage(
        branchId: Int?,
        from: Long,
        to: Long,
        page: Int,
        size: Int
    ): PageResponseDto<RestockReportItemDto> {
        val pageable = pageRequest(page, size)

        val result = restockLogRepository.getRestockReportRowsPage(
            branchId = branchId,
            from = from,
            to = to,
            pageable = pageable
        ).map {
            RestockReportItemDto(
                restockId = it.restockId,
                ingredientId = it.ingredientId,
                ingredientName = it.ingredientName,
                quantityAdded = it.quantityAdded,
                unitType = it.unitType,
                supplier = it.supplier,
                userId = it.userId,
                userName = it.userName,
                dateTime = it.dateTime
            )
        }

        return PageResponseDto.fromPage(result)
    }

    fun inventoryAdjustmentReport(
        branchId: Int,
        from: Long,
        to: Long
    ): InventoryAdjustmentReportDto {
        val branch = branchRepository.findById(branchId).orElse(null)

        val items = inventoryAdjustmentRepository.getInventoryAdjustmentReportRows(
            branchId = branchId,
            from = from,
            to = to
        ).map {
            InventoryAdjustmentReportItemDto(
                adjustmentId = it.adjustmentId,
                ingredientId = it.ingredientId,
                ingredientName = it.ingredientName,
                adjustmentAmount = it.adjustmentAmount,
                unitType = it.unitType,
                reason = it.reason,
                userId = it.userId,
                userName = it.userName,
                dateTime = it.dateTime
            )
        }

        return InventoryAdjustmentReportDto(
            branchId = branchId,
            branchName = branch?.branchName ?: "Branch $branchId",
            from = from,
            to = to,
            totalAdjustmentAmount = items.sumOf { it.adjustmentAmount },
            items = items
        )
    }

    fun inventoryAdjustmentSummary(
        branchId: Int?,
        from: Long,
        to: Long
    ): InventoryAdjustmentSummaryDto {
        val summary = inventoryAdjustmentRepository.getInventoryAdjustmentSummary(
            branchId = branchId,
            from = from,
            to = to
        )

        return InventoryAdjustmentSummaryDto(
            branchId = branchId,
            branchName = branchName(branchId),
            from = from,
            to = to,
            totalAdjustmentAmount = summary.totalAdjustmentAmount ?: 0.0,
            totalAdjustmentEntries = summary.totalAdjustmentEntries ?: 0L
        )
    }

    fun inventoryAdjustmentPage(
        branchId: Int?,
        from: Long,
        to: Long,
        page: Int,
        size: Int
    ): PageResponseDto<InventoryAdjustmentReportItemDto> {
        val pageable = pageRequest(page, size)

        val result = inventoryAdjustmentRepository.getInventoryAdjustmentReportRowsPage(
            branchId = branchId,
            from = from,
            to = to,
            pageable = pageable
        ).map {
            InventoryAdjustmentReportItemDto(
                adjustmentId = it.adjustmentId,
                ingredientId = it.ingredientId,
                ingredientName = it.ingredientName,
                adjustmentAmount = it.adjustmentAmount,
                unitType = it.unitType,
                reason = it.reason,
                userId = it.userId,
                userName = it.userName,
                dateTime = it.dateTime
            )
        }

        return PageResponseDto.fromPage(result)
    }

    fun transactionReport(
        branchId: Int,
        from: Long,
        to: Long
    ): TransactionReportDto {
        val branch = branchRepository.findById(branchId).orElse(null)

        val transactions = transactionRepository.getTransactionReportRows(
            branchId = branchId,
            from = from,
            to = to
        )

        val rows = attachTransactionLines(transactions)

        return TransactionReportDto(
            branchId = branchId,
            branchName = branch?.branchName ?: "Branch $branchId",
            from = from,
            to = to,
            transactions = rows
        )
    }

    fun combinedTransactionReport(
        from: Long,
        to: Long
    ): TransactionReportDto {
        val transactions = transactionRepository.getAllTransactionReportRows(
            from = from,
            to = to
        )

        val rows = attachTransactionLines(transactions)

        return TransactionReportDto(
            branchId = null,
            branchName = "All Branches",
            from = from,
            to = to,
            transactions = rows
        )
    }

    fun transactionPage(
        branchId: Int?,
        from: Long,
        to: Long,
        page: Int,
        size: Int
    ): PageResponseDto<TransactionReportItemDto> {
        val pageable = pageRequest(page, size)

        val transactionPage = transactionRepository.getTransactionReportRowsPage(
            branchId = branchId,
            from = from,
            to = to,
            pageable = pageable
        )

        val content = attachTransactionLines(transactionPage.content)

        val mappedPage = PageImpl(
            content,
            pageable,
            transactionPage.totalElements
        )

        return PageResponseDto.fromPage(mappedPage)
    }

    fun staffLogsReport(
        branchId: Int,
        from: Long,
        to: Long
    ): StaffLogReportDto {
        val branch = branchRepository.findById(branchId).orElse(null)

        val logs = staffLogRepository.getStaffLogReportRows(
            branchId = branchId,
            from = from,
            to = to
        ).map {
            StaffLogReportItemDto(
                logId = it.logId,
                userId = it.userId,
                userName = it.userName,
                clockIn = it.clockIn,
                clockOut = it.clockOut,
                image = it.image
            )
        }

        return StaffLogReportDto(
            branchId = branchId,
            branchName = branch?.branchName ?: "Branch $branchId",
            from = from,
            to = to,
            logs = logs
        )
    }

    fun staffLogsPage(
        branchId: Int?,
        from: Long,
        to: Long,
        page: Int,
        size: Int
    ): PageResponseDto<StaffLogReportItemDto> {
        val pageable = pageRequest(page, size)

        val result = staffLogRepository.getStaffLogReportRowsPage(
            branchId = branchId,
            from = from,
            to = to,
            pageable = pageable
        ).map {
            StaffLogReportItemDto(
                logId = it.logId,
                userId = it.userId,
                userName = it.userName,
                clockIn = it.clockIn,
                clockOut = it.clockOut,
                image = null
            )
        }

        return PageResponseDto.fromPage(result)
    }

    fun auditLogsReport(
        branchId: Int,
        from: Long,
        to: Long
    ): AuditLogReportDto {
        val branch = branchRepository.findById(branchId).orElse(null)

        val logs = auditLogRepository.getAuditLogReportRows(
            branchId = branchId,
            from = from,
            to = to
        ).map {
            AuditLogReportItemDto(
                logId = it.logId,
                userId = it.userId,
                userName = it.userName,
                action = it.action,
                tableAffected = it.tableAffected,
                timestamp = it.timestamp
            )
        }

        return AuditLogReportDto(
            branchId = branchId,
            branchName = branch?.branchName ?: "Branch $branchId",
            from = from,
            to = to,
            logs = logs
        )
    }

    fun auditLogsPage(
        branchId: Int?,
        from: Long,
        to: Long,
        page: Int,
        size: Int
    ): PageResponseDto<AuditLogReportItemDto> {
        val pageable = pageRequest(page, size)

        val result = auditLogRepository.getAuditLogReportRowsPage(
            branchId = branchId,
            from = from,
            to = to,
            pageable = pageable
        ).map {
            AuditLogReportItemDto(
                logId = it.logId,
                userId = it.userId,
                userName = it.userName,
                action = it.action,
                tableAffected = it.tableAffected,
                timestamp = it.timestamp
            )
        }

        return PageResponseDto.fromPage(result)
    }

    private fun attachTransactionLines(
        transactions: List<fruitylicious.repository.report.TransactionReportRow>
    ): List<TransactionReportItemDto> {
        val transactionIds = transactions.map { it.transactionId }

        val linesByTransaction = if (transactionIds.isEmpty()) {
            emptyMap()
        } else {
            transactionItemRepository.getTransactionLines(transactionIds)
                .groupBy { it.transactionId }
        }

        return transactions.map { transaction ->
            val lines = linesByTransaction[transaction.transactionId]
                .orEmpty()
                .map { line ->
                    TransactionLineReportDto(
                        productId = line.productId,
                        productName = line.productName,
                        quantity = line.quantity,
                        subtotal = line.subtotal,
                        sizeName = line.sizeName,
                        addons = emptyList()
                    )
                }

            TransactionReportItemDto(
                transactionId = transaction.transactionId,
                userId = transaction.userId,
                userName = transaction.userName,
                branchId = transaction.branchId,
                totalAmount = transaction.totalAmount,
                paymentType = transaction.paymentType,
                dateTime = transaction.dateTime,
                status = transaction.status,
                items = lines
            )
        }
    }

    private fun pageRequest(
        page: Int,
        size: Int
    ): PageRequest {
        return PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, 100)
        )
    }

    private fun branchName(branchId: Int?): String {
        return branchId?.let {
            branchRepository.findById(it).orElse(null)?.branchName ?: "Branch $it"
        } ?: "All Branches"
    }
}