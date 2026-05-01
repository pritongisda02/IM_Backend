package fruitylicious.controller

import fruitylicious.dto.AuditLogReportResponse
import fruitylicious.dto.CombinedSalesReportResponse
import fruitylicious.dto.InventoryReportResponse
import fruitylicious.dto.RestockReportResponse
import fruitylicious.dto.SalesReportResponse
import fruitylicious.dto.StaffLogReportResponse
import fruitylicious.dto.TransactionReportResponse
import fruitylicious.dto.WasteReportResponse
import fruitylicious.service.ReportService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
class ReportController(
    private val reportService: ReportService
) {

    @GetMapping("/sales")
    fun getSalesReport(
        @RequestParam branchId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): ResponseEntity<SalesReportResponse> {
        return ResponseEntity.ok(reportService.getSalesReport(branchId, from, to))
    }

    @GetMapping("/sales/combined")
    fun getCombinedSalesReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): ResponseEntity<CombinedSalesReportResponse> {
        return ResponseEntity.ok(reportService.getCombinedSalesReport(from, to))
    }

    @GetMapping("/waste")
    fun getWasteReport(
        @RequestParam branchId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): ResponseEntity<WasteReportResponse> {
        return ResponseEntity.ok(reportService.getWasteReport(branchId, from, to))
    }

    @GetMapping("/restock")
    fun getRestockReport(
        @RequestParam branchId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): ResponseEntity<RestockReportResponse> {
        return ResponseEntity.ok(reportService.getRestockReport(branchId, from, to))
    }

    @GetMapping("/inventory")
    fun getInventoryReport(
        @RequestParam branchId: Long
    ): ResponseEntity<InventoryReportResponse> {
        return ResponseEntity.ok(reportService.getInventoryReport(branchId))
    }

    @GetMapping("/transactions")
    fun getTransactionReport(
        @RequestParam branchId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): ResponseEntity<TransactionReportResponse> {
        return ResponseEntity.ok(reportService.getTransactionReport(branchId, from, to))
    }

    @GetMapping("/staff-logs")
    fun getStaffLogReport(
        @RequestParam branchId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): ResponseEntity<StaffLogReportResponse> {
        return ResponseEntity.ok(reportService.getStaffLogReport(branchId, from, to))
    }

    @GetMapping("/audit-logs")
    fun getAuditLogReport(
        @RequestParam branchId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): ResponseEntity<AuditLogReportResponse> {
        return ResponseEntity.ok(reportService.getAuditLogReport(branchId, from, to))
    }
}