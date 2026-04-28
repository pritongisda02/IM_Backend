package fruitylicious.controller

import fruitylicious.config.JwtTokenProvider
import fruitylicious.dto.WasteRequest
import fruitylicious.dto.WasteResponse
import fruitylicious.service.WasteService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/waste")
class WasteController(
    private val wasteService: WasteService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    // -------------------------------------------------------------------------
    // GET /api/waste
    // Staff sees their branch only; admin may pass ?branchId to filter
    // -------------------------------------------------------------------------

    @GetMapping
    fun getAll(
        httpRequest: HttpServletRequest,
        @RequestParam(required = false) branchId: Long?
    ): ResponseEntity<List<WasteResponse>> {
        val info              = resolveToken(httpRequest)
        val effectiveBranchId = if (info.role == "ADMIN" && branchId != null) branchId
        else info.branchId
        return ResponseEntity.ok(wasteService.getAll(effectiveBranchId))
    }

    // -------------------------------------------------------------------------
    // POST /api/waste
    // -------------------------------------------------------------------------

    @PostMapping
    fun logWaste(
        @Valid @RequestBody request: WasteRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<WasteResponse> {
        val info     = resolveToken(httpRequest)
        val response = wasteService.logWaste(request, info.userId, info.branchId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
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