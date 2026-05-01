package fruitylicious.reports

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
}