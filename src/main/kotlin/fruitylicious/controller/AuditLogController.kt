package fruitylicious.controller

import fruitylicious.config.JwtTokenProvider
import fruitylicious.dto.AuditLogResponse
import fruitylicious.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
class AuditLogController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    // -------------------------------------------------------------------------
    // GET /api/admin/audit-logs
    // Admin can filter by ?branchId; omitting returns all branches
    // -------------------------------------------------------------------------

    @GetMapping
    fun getAuditLogs(
        httpRequest: HttpServletRequest,
        @RequestParam(required = false) branchId: Long?
    ): ResponseEntity<List<AuditLogResponse>> {
        val token   = httpRequest.getHeader("Authorization").substring(7)
        val role    = jwtTokenProvider.getRoleFromToken(token).uppercase()
        val isAdmin = role == "ADMIN"

        // Admin with no branchId filter → all logs; with branchId → filtered
        val response = userService.getAuditLogs(
            branchId = branchId,
            isAdmin  = isAdmin && branchId == null
        )
        return ResponseEntity.ok(response)
    }
}