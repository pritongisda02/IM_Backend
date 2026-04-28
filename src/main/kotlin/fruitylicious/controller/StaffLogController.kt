package fruitylicious.controller

import fruitylicious.config.JwtTokenProvider
import fruitylicious.dto.ClockInResponse
import fruitylicious.dto.ClockOutResponse
import fruitylicious.dto.StaffLogResponse
import fruitylicious.service.StaffService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/staff-logs")
class StaffLogController(
    private val staffService: StaffService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    // -------------------------------------------------------------------------
    // POST /api/staff-logs/clock-in
    // -------------------------------------------------------------------------

    @PostMapping("/clock-in")
    fun clockIn(
        httpRequest: HttpServletRequest
    ): ResponseEntity<ClockInResponse> {
        val info     = resolveToken(httpRequest)
        val response = staffService.clockIn(info.userId, info.branchId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // -------------------------------------------------------------------------
    // POST /api/staff-logs/clock-out
    // -------------------------------------------------------------------------

    @PostMapping("/clock-out")
    fun clockOut(
        httpRequest: HttpServletRequest
    ): ResponseEntity<ClockOutResponse> {
        val info     = resolveToken(httpRequest)
        val response = staffService.clockOut(info.userId, info.branchId)
        return ResponseEntity.ok(response)
    }

    // -------------------------------------------------------------------------
    // GET /api/staff-logs
    // Staff sees their own branch; admin may pass ?branchId to filter
    // -------------------------------------------------------------------------

    @GetMapping
    fun getLogs(
        httpRequest: HttpServletRequest,
        @RequestParam(required = false) branchId: Long?
    ): ResponseEntity<List<StaffLogResponse>> {
        val info              = resolveToken(httpRequest)
        val isAdmin           = info.role == "ADMIN"
        val effectiveBranchId = if (isAdmin && branchId != null) branchId
        else info.branchId
        val response = staffService.getLogs(
            branchId           = effectiveBranchId,
            isAdmin            = isAdmin,
            requestingBranchId = effectiveBranchId
        )
        return ResponseEntity.ok(response)
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    data class TokenInfo(
        val userId: Long,
        val role: String,
        val branchId: Long
    )

    private fun resolveToken(request: HttpServletRequest): TokenInfo {
        val token    = request.getHeader("Authorization").substring(7)
        val userId   = jwtTokenProvider.getUserIdFromToken(token)
        val role     = jwtTokenProvider.getRoleFromToken(token).uppercase()
        val branchId = jwtTokenProvider.getBranchIdFromToken(token) ?: 0L
        return TokenInfo(userId, role, branchId)
    }
}