package fruitylicious.reports

import fruitylicious.repository.AuditLogRepository
import fruitylicious.repository.BranchRepository
import fruitylicious.repository.InventoryRepository
import fruitylicious.repository.RestockLogRepository
import fruitylicious.repository.StaffLogRepository
import fruitylicious.repository.TransactionItemRepository
import fruitylicious.repository.TransactionRepository
import fruitylicious.repository.WasteLogRepository
import org.springframework.stereotype.Service

@Service
class ReportsService(
    private val branchRepository: BranchRepository,
    private val inventoryRepository: InventoryRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionItemRepository: TransactionItemRepository,
    private val wasteLogRepository: WasteLogRepository,
    private val restockLogRepository: RestockLogRepository,
    private val staffLogRepository: StaffLogRepository,
    private val auditLogRepository: AuditLogRepository
) {

    fun salesReport(
        branchId: Int?,
        from: Long,
        to: Long
    ): SalesReportDto {
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
            branchName = branchName,
            from = from,
            to = to,
            totalSales = totalSales,
            totalTransactions = totalTransactions,
            averageTransactionValue = if (totalTransactions > 0) {
                totalSales / totalTransactions
            } else {
                0.0
            },
            items = items
        )
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
                dateTime = it.dateTime
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

        val transactionIds = transactions.map { it.transactionId }

        val linesByTransaction = if (transactionIds.isEmpty()) {
            emptyMap()
        } else {
            transactionItemRepository.getTransactionLines(transactionIds)
                .groupBy { it.transactionId }
        }

        val rows = transactions.map { transaction ->
            val lines = linesByTransaction[transaction.transactionId]
                .orEmpty()
                .map { line ->
                    TransactionLineReportDto(
                        productId = line.productId,
                        productName = line.productName,
                        quantity = line.quantity,
                        subtotal = line.subtotal
                    )
                }

            TransactionReportItemDto(
                transactionId = transaction.transactionId,
                userId = transaction.userId,
                userName = transaction.userName,
                totalAmount = transaction.totalAmount,
                paymentType = transaction.paymentType,
                dateTime = transaction.dateTime,
                status = transaction.status,
                items = lines
            )
        }

        return TransactionReportDto(
            branchId = branchId,
            branchName = branch?.branchName ?: "Branch $branchId",
            from = from,
            to = to,
            transactions = rows
        )
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
}