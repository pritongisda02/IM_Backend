package fruitylicious.dto

import java.math.BigDecimal
import java.time.Instant

data class SalesReportResponse(
    val branchId: Long,
    val branchName: String,
    val from: Instant,
    val to: Instant,
    val totalSales: BigDecimal,
    val transactionCount: Int
)

data class CombinedSalesReportResponse(
    val from: Instant,
    val to: Instant,
    val totalSales: BigDecimal,
    val transactionCount: Int,
    val perBranch: List<SalesReportResponse>
)

data class WasteReportResponse(
    val branchId: Long,
    val branchName: String,
    val from: Instant,
    val to: Instant,
    val entries: List<WasteLogResponse>
)

data class RestockReportResponse(
    val branchId: Long,
    val branchName: String,
    val from: Instant,
    val to: Instant,
    val entries: List<RestockLogResponse>
)

data class InventoryReportResponse(
    val branchId: Long,
    val branchName: String,
    val items: List<InventoryResponse>
)

data class TransactionReportResponse(
    val branchId: Long,
    val branchName: String,
    val from: Instant,
    val to: Instant,
    val transactions: List<TransactionResponse>
)

data class StaffLogReportResponse(
    val branchId: Long,
    val branchName: String,
    val from: Instant,
    val to: Instant,
    val entries: List<StaffLogResponse>
)

data class AuditLogReportResponse(
    val branchId: Long,
    val branchName: String,
    val from: Instant,
    val to: Instant,
    val entries: List<AuditLogResponse>
)