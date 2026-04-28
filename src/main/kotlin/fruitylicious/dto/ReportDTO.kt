package Fruitylicous.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

// --- Sales Report ---

data class SalesReportRequest(
    val branchId: Long? = null,       // null = all branches (admin only)
    val from: LocalDate,
    val to: LocalDate
)

data class DailySalesSummary(
    val date: LocalDate,
    val branchId: Long,
    val branchName: String,
    val totalTransactions: Int,
    val totalRevenue: BigDecimal,
    val voidedTransactions: Int
)

data class TopProductEntry(
    val productId: Long,
    val productName: String,
    val totalQuantitySold: Int,
    val totalRevenue: BigDecimal
)

data class SalesReportResponse(
    val from: LocalDate,
    val to: LocalDate,
    val branchId: Long?,
    val totalRevenue: BigDecimal,
    val totalTransactions: Int,
    val voidedTransactions: Int,
    val dailySummaries: List<DailySalesSummary>,
    val topProducts: List<TopProductEntry>
)

// --- Waste Report ---

data class WasteReportRequest(
    val branchId: Long? = null,
    val from: LocalDate,
    val to: LocalDate
)

data class WasteReportEntry(
    val ingredientId: Long,
    val ingredientName: String,
    val unitType: String,
    val branchId: Long,
    val branchName: String,
    val totalQuantityWasted: BigDecimal,
    val entryCount: Int
)

data class WasteReportResponse(
    val from: LocalDate,
    val to: LocalDate,
    val branchId: Long?,
    val entries: List<WasteReportEntry>
)

// --- Restock Report ---

data class RestockReportRequest(
    val branchId: Long? = null,
    val from: LocalDate,
    val to: LocalDate
)

data class RestockReportEntry(
    val ingredientId: Long,
    val ingredientName: String,
    val unitType: String,
    val branchId: Long,
    val branchName: String,
    val totalQuantityRestocked: BigDecimal,
    val restockCount: Int,
    val lastRestockDate: LocalDateTime
)

data class RestockReportResponse(
    val from: LocalDate,
    val to: LocalDate,
    val branchId: Long?,
    val entries: List<RestockReportEntry>
)