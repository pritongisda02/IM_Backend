package fruitylicious.controller

import fruitylicious.dto.RestockReportResponse
import fruitylicious.dto.SalesReportResponse
import fruitylicious.dto.WasteReportResponse
import fruitylicious.service.ReportService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
class ReportController(
    private val reportService: ReportService
) {

    // -------------------------------------------------------------------------
    // GET /api/admin/reports/sales
    // ?from=YYYY-MM-DD&to=YYYY-MM-DD&branchId= (optional)
    // -------------------------------------------------------------------------

    @GetMapping("/sales")
    fun getSalesReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate,
        @RequestParam(required = false) branchId: Long?
    ): ResponseEntity<SalesReportResponse> {
        if (from.isAfter(to)) {
            throw IllegalArgumentException("'from' date must not be after 'to' date")
        }
        return ResponseEntity.ok(reportService.getSalesReport(branchId, from, to))
    }

    // -------------------------------------------------------------------------
    // GET /api/admin/reports/waste
    // ?from=YYYY-MM-DD&to=YYYY-MM-DD&branchId= (optional)
    // -------------------------------------------------------------------------

    @GetMapping("/waste")
    fun getWasteReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate,
        @RequestParam(required = false) branchId: Long?
    ): ResponseEntity<WasteReportResponse> {
        if (from.isAfter(to)) {
            throw IllegalArgumentException("'from' date must not be after 'to' date")
        }
        return ResponseEntity.ok(reportService.getWasteReport(branchId, from, to))
    }

    // -------------------------------------------------------------------------
    // GET /api/admin/reports/restock
    // ?from=YYYY-MM-DD&to=YYYY-MM-DD&branchId= (optional)
    // -------------------------------------------------------------------------

    @GetMapping("/restock")
    fun getRestockReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate,
        @RequestParam(required = false) branchId: Long?
    ): ResponseEntity<RestockReportResponse> {
        if (from.isAfter(to)) {
            throw IllegalArgumentException("'from' date must not be after 'to' date")
        }
        return ResponseEntity.ok(reportService.getRestockReport(branchId, from, to))
    }
}