package fruitylicious.reports

import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reports")
class ReportsController(
    private val reportsService: ReportsService
) {

    @GetMapping("/sales")
    fun getSalesReport(
        @RequestParam branchId: Int,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<SalesReportDto> {
        return ResponseEntity.ok(
            reportsService.salesReport(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/sales/combined")
    fun getCombinedSalesReport(
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<SalesReportDto> {
        return ResponseEntity.ok(
            reportsService.salesReport(
                branchId = null,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/sales/summary")
    fun getSalesSummary(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<SalesSummaryDto> {
        return ResponseEntity.ok(
            reportsService.salesSummary(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/sales/items")
    fun getSalesItems(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<PageResponseDto<SalesReportItemDto>> {
        return ResponseEntity.ok(
            reportsService.salesItemsPage(
                branchId = branchId,
                from = from,
                to = to,
                page = page,
                size = size
            )
        )
    }

    @GetMapping("/sales/top-items")
    fun getTopSellingItems(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long,
        @RequestParam(defaultValue = "5") limit: Int
    ): ResponseEntity<List<SalesReportItemDto>> {
        return ResponseEntity.ok(
            reportsService.topSellingItems(
                branchId = branchId,
                from = from,
                to = to,
                limit = limit
            )
        )
    }

    @GetMapping("/waste")
    fun getWasteReport(
        @RequestParam branchId: Int,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<WasteReportDto> {
        return ResponseEntity.ok(
            reportsService.wasteReport(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/waste/summary")
    fun getWasteSummary(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<WasteSummaryDto> {
        return ResponseEntity.ok(
            reportsService.wasteSummary(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/waste/page")
    fun getWastePage(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<PageResponseDto<WasteReportItemDto>> {
        return ResponseEntity.ok(
            reportsService.wastePage(
                branchId = branchId,
                from = from,
                to = to,
                page = page,
                size = size
            )
        )
    }

    @GetMapping("/restock")
    fun getRestockReport(
        @RequestParam branchId: Int,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<RestockReportDto> {
        return ResponseEntity.ok(
            reportsService.restockReport(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/restock/summary")
    fun getRestockSummary(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<RestockSummaryDto> {
        return ResponseEntity.ok(
            reportsService.restockSummary(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/restock/page")
    fun getRestockPage(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<PageResponseDto<RestockReportItemDto>> {
        return ResponseEntity.ok(
            reportsService.restockPage(
                branchId = branchId,
                from = from,
                to = to,
                page = page,
                size = size
            )
        )
    }

    @GetMapping("/inventory")
    fun getInventoryReport(
        @RequestParam branchId: Int
    ): ResponseEntity<InventoryReportDto> {
        return ResponseEntity.ok(
            reportsService.inventoryReport(
                branchId = branchId
            )
        )
    }

    @GetMapping("/inventory-adjustments")
    fun getInventoryAdjustmentReport(
        @RequestParam branchId: Int,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<InventoryAdjustmentReportDto> {
        return ResponseEntity.ok(
            reportsService.inventoryAdjustmentReport(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/inventory-adjustments/summary")
    fun getInventoryAdjustmentSummary(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<InventoryAdjustmentSummaryDto> {
        return ResponseEntity.ok(
            reportsService.inventoryAdjustmentSummary(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/inventory-adjustments/page")
    fun getInventoryAdjustmentPage(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<PageResponseDto<InventoryAdjustmentReportItemDto>> {
        return ResponseEntity.ok(
            reportsService.inventoryAdjustmentPage(
                branchId = branchId,
                from = from,
                to = to,
                page = page,
                size = size
            )
        )
    }

    @GetMapping("/transactions")
    fun getTransactionReport(
        @RequestParam branchId: Int,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<TransactionReportDto> {
        return ResponseEntity.ok(
            reportsService.transactionReport(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/transactions/combined")
    fun getCombinedTransactionReport(
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<TransactionReportDto> {
        return ResponseEntity.ok(
            reportsService.combinedTransactionReport(
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/transactions/page")
    fun getTransactionPage(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<PageResponseDto<TransactionReportItemDto>> {
        return ResponseEntity.ok(
            reportsService.transactionPage(
                branchId = branchId,
                from = from,
                to = to,
                page = page,
                size = size
            )
        )
    }

    @GetMapping("/staff-logs")
    fun getStaffLogsReport(
        @RequestParam branchId: Int,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<StaffLogReportDto> {
        return ResponseEntity.ok(
            reportsService.staffLogsReport(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/staff-logs/page")
    fun getStaffLogsPage(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<PageResponseDto<StaffLogReportItemDto>> {
        return ResponseEntity.ok(
            reportsService.staffLogsPage(
                branchId = branchId,
                from = from,
                to = to,
                page = page,
                size = size
            )
        )
    }

    @GetMapping("/audit-logs")
    fun getAuditLogsReport(
        @RequestParam branchId: Int,
        @RequestParam from: Long,
        @RequestParam to: Long
    ): ResponseEntity<AuditLogReportDto> {
        return ResponseEntity.ok(
            reportsService.auditLogsReport(
                branchId = branchId,
                from = from,
                to = to
            )
        )
    }

    @GetMapping("/audit-logs/page")
    fun getAuditLogsPage(
        @RequestParam(required = false) branchId: Int?,
        @RequestParam from: Long,
        @RequestParam to: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<PageResponseDto<AuditLogReportItemDto>> {
        return ResponseEntity.ok(
            reportsService.auditLogsPage(
                branchId = branchId,
                from = from,
                to = to,
                page = page,
                size = size
            )
        )
    }
}