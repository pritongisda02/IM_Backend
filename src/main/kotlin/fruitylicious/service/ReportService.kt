package fruitylicious.service

import fruitylicious.dto.DailySalesSummary
import fruitylicious.dto.RestockReportEntry
import fruitylicious.dto.RestockReportResponse
import fruitylicious.dto.SalesReportResponse
import fruitylicious.dto.TopProductEntry
import fruitylicious.dto.WasteReportEntry
import fruitylicious.dto.WasteReportResponse
import fruitylicious.repository.local.LocalBranchRepository
import fruitylicious.repository.local.LocalIngredientRepository
import fruitylicious.repository.local.LocalProductRepository
import fruitylicious.repository.local.LocalRestockLogRepository
import fruitylicious.repository.local.LocalTransactionItemRepository
import fruitylicious.repository.local.LocalTransactionRepository
import fruitylicious.repository.local.LocalWasteLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@Service
class ReportService(
    private val localTransactionRepository: LocalTransactionRepository,
    private val localTransactionItemRepository: LocalTransactionItemRepository,
    private val localWasteLogRepository: LocalWasteLogRepository,
    private val localRestockLogRepository: LocalRestockLogRepository,
    private val localProductRepository: LocalProductRepository,
    private val localIngredientRepository: LocalIngredientRepository,
    private val localBranchRepository: LocalBranchRepository
) {

    // -------------------------------------------------------------------------
    // Sales Report
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getSalesReport(
        branchId: Long?,
        from: LocalDate,
        to: LocalDate
    ): SalesReportResponse {
        val fromDt = from.atStartOfDay()
        val toDt   = to.atTime(LocalTime.MAX)

        // Fetch transactions
        val transactions = if (branchId != null) {
            localTransactionRepository.findAllByBranchIdAndDateTimeBetween(branchId, fromDt, toDt)
        } else {
            localTransactionRepository.findAllByDateTimeBetween(fromDt, toDt)
        }

        val completed = transactions.filter { it.status == "completed" }
        val voided    = transactions.filter { it.status == "void" }
        val totalRevenue = completed.fold(BigDecimal.ZERO) { acc, t -> acc.add(t.totalAmount) }

        // Daily summaries
        val branches = localBranchRepository.findAll().associateBy { it.branchId }

        val dailySummaries = completed
            .groupBy { Pair(it.dateTime.toLocalDate(), it.branchId) }
            .map { (key, txns) ->
                val (date, bid) = key
                val branchVoided = voided.count { it.branchId == bid &&
                        it.dateTime.toLocalDate() == date }
                DailySalesSummary(
                    date                = date,
                    branchId            = bid,
                    branchName          = branches[bid]?.branchName ?: "Unknown",
                    totalTransactions   = txns.size,
                    totalRevenue        = txns.fold(BigDecimal.ZERO) { a, t -> a.add(t.totalAmount) },
                    voidedTransactions  = branchVoided
                )
            }
            .sortedWith(compareBy({ it.date }, { it.branchId }))

        // Top products
        val items = if (branchId != null) {
            localTransactionItemRepository.findCompletedItemsByBranchAndPeriod(branchId, fromDt, toDt)
        } else {
            localTransactionItemRepository.findCompletedItemsAllBranchesAndPeriod(fromDt, toDt)
        }

        val topProducts = items
            .groupBy { it.productId }
            .map { (pid, itemList) ->
                val product = localProductRepository.findById(pid).orElse(null)
                TopProductEntry(
                    productId          = pid,
                    productName        = product?.productName ?: "Unknown",
                    totalQuantitySold  = itemList.sumOf { it.quantity },
                    totalRevenue       = itemList.fold(BigDecimal.ZERO) { a, i -> a.add(i.subtotal) }
                )
            }
            .sortedByDescending { it.totalQuantitySold }
            .take(10)

        return SalesReportResponse(
            from                = from,
            to                  = to,
            branchId            = branchId,
            totalRevenue        = totalRevenue,
            totalTransactions   = completed.size,
            voidedTransactions  = voided.size,
            dailySummaries      = dailySummaries,
            topProducts         = topProducts
        )
    }

    // -------------------------------------------------------------------------
    // Waste Report
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getWasteReport(
        branchId: Long?,
        from: LocalDate,
        to: LocalDate
    ): WasteReportResponse {
        val fromDt = from.atStartOfDay()
        val toDt   = to.atTime(LocalTime.MAX)

        val wasteLogs = if (branchId != null) {
            localWasteLogRepository.findAllByBranchIdAndDateTimeBetween(branchId, fromDt, toDt)
        } else {
            localWasteLogRepository.findAllByDateTimeBetween(fromDt, toDt)
        }

        val branches    = localBranchRepository.findAll().associateBy { it.branchId }
        val ingredients = localIngredientRepository.findAll().associateBy { it.ingredientId }

        val entries = wasteLogs
            .groupBy { Pair(it.ingredientId, it.branchId) }
            .map { (key, logs) ->
                val (ingId, bid) = key
                val ingredient   = ingredients[ingId]
                val branch       = branches[bid]
                WasteReportEntry(
                    ingredientId          = ingId,
                    ingredientName        = ingredient?.ingredientName ?: "Unknown",
                    unitType              = ingredient?.unitType ?: "",
                    branchId              = bid,
                    branchName            = branch?.branchName ?: "Unknown",
                    totalQuantityWasted   = logs.fold(BigDecimal.ZERO) { a, l -> a.add(l.quantity) },
                    entryCount            = logs.size
                )
            }
            .sortedByDescending { it.totalQuantityWasted }

        return WasteReportResponse(
            from     = from,
            to       = to,
            branchId = branchId,
            entries  = entries
        )
    }

    // -------------------------------------------------------------------------
    // Restock Report
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getRestockReport(
        branchId: Long?,
        from: LocalDate,
        to: LocalDate
    ): RestockReportResponse {
        val fromDt = from.atStartOfDay()
        val toDt   = to.atTime(LocalTime.MAX)

        val restockLogs = if (branchId != null) {
            localRestockLogRepository.findAllByBranchIdAndDateTimeBetween(branchId, fromDt, toDt)
        } else {
            localRestockLogRepository.findAllByDateTimeBetween(fromDt, toDt)
        }

        val branches    = localBranchRepository.findAll().associateBy { it.branchId }
        val ingredients = localIngredientRepository.findAll().associateBy { it.ingredientId }

        val entries = restockLogs
            .groupBy { Pair(it.ingredientId, it.branchId) }
            .map { (key, logs) ->
                val (ingId, bid) = key
                val ingredient   = ingredients[ingId]
                val branch       = branches[bid]
                RestockReportEntry(
                    ingredientId           = ingId,
                    ingredientName         = ingredient?.ingredientName ?: "Unknown",
                    unitType               = ingredient?.unitType ?: "",
                    branchId               = bid,
                    branchName             = branch?.branchName ?: "Unknown",
                    totalQuantityRestocked = logs.fold(BigDecimal.ZERO) { a, l -> a.add(l.quantityAdded) },
                    restockCount           = logs.size,
                    lastRestockDate        = logs.maxOf { it.dateTime }
                )
            }
            .sortedByDescending { it.totalQuantityRestocked }

        return RestockReportResponse(
            from     = from,
            to       = to,
            branchId = branchId,
            entries  = entries
        )
    }
}