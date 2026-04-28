package fruitylicious.controller

import fruitylicious.config.JwtTokenProvider
import fruitylicious.dto.AdjustmentRequest
import fruitylicious.dto.AdjustmentResponse
import fruitylicious.dto.InventoryResponse
import fruitylicious.dto.RestockRequest
import fruitylicious.dto.RestockResponse
import fruitylicious.service.InventoryService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/inventory")
class InventoryController(
    private val inventoryService: InventoryService,
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${app.sync.low-stock-threshold}") private val defaultThreshold: Int
) {

    // -------------------------------------------------------------------------
    // GET /api/inventory
    // Returns inventory for the current branch (staff) or all (admin by branchId param)
    // -------------------------------------------------------------------------

    @GetMapping
    fun getInventory(
        httpRequest: HttpServletRequest,
        @RequestParam(required = false) branchId: Long?
    ): ResponseEntity<List<InventoryResponse>> {
        val (_, role, _, tokenBranchId) = resolveToken(httpRequest)
        val effectiveBranchId = if (role == "ADMIN" && branchId != null) branchId
        else tokenBranchId
        return ResponseEntity.ok(inventoryService.getInventory(effectiveBranchId))
    }

    // -------------------------------------------------------------------------
    // GET /api/inventory/low-stock?threshold=
    // -------------------------------------------------------------------------

    @GetMapping("/low-stock")
    fun getLowStock(
        httpRequest: HttpServletRequest,
        @RequestParam(required = false) threshold: Int?,
        @RequestParam(required = false) branchId: Long?
    ): ResponseEntity<List<InventoryResponse>> {
        val (_, role, _, tokenBranchId) = resolveToken(httpRequest)
        val effectiveBranchId = if (role == "ADMIN" && branchId != null) branchId
        else tokenBranchId
        val effectiveThreshold = BigDecimal(threshold ?: defaultThreshold)
        return ResponseEntity.ok(
            inventoryService.getLowStock(effectiveBranchId, effectiveThreshold)
        )
    }

    // -------------------------------------------------------------------------
    // POST /api/inventory/restock
    // -------------------------------------------------------------------------

    @PostMapping("/restock")
    fun restock(
        @Valid @RequestBody request: RestockRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<RestockResponse> {
        val (userId, _, _, branchId) = resolveToken(httpRequest)
        val response = inventoryService.restock(request, userId, branchId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // -------------------------------------------------------------------------
    // POST /api/inventory/adjust
    // -------------------------------------------------------------------------

    @PostMapping("/adjust")
    fun adjust(
        @Valid @RequestBody request: AdjustmentRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<AdjustmentResponse> {
        val (userId, _, _, branchId) = resolveToken(httpRequest)
        val response = inventoryService.adjust(request, userId, branchId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // -------------------------------------------------------------------------
    // GET /api/inventory/restock/history
    // -------------------------------------------------------------------------

    @GetMapping("/restock/history")
    fun getRestockHistory(
        httpRequest: HttpServletRequest,
        @RequestParam(required = false) branchId: Long?
    ): ResponseEntity<List<RestockResponse>> {
        val (_, role, _, tokenBranchId) = resolveToken(httpRequest)
        val effectiveBranchId = if (role == "ADMIN" && branchId != null) branchId
        else tokenBranchId
        return ResponseEntity.ok(inventoryService.getRestockHistory(effectiveBranchId))
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    data class TokenInfo(
        val userId: Long,
        val role: String,
        val username: String,
        val branchId: Long
    )

    private fun resolveToken(request: HttpServletRequest): TokenInfo {
        val token    = request.getHeader("Authorization").substring(7)
        val userId   = jwtTokenProvider.getUserIdFromToken(token)
        val role     = jwtTokenProvider.getRoleFromToken(token).uppercase()
        val username = jwtTokenProvider.getUsernameFromToken(token)
        val branchId = jwtTokenProvider.getBranchIdFromToken(token) ?: 0L
        return TokenInfo(userId, role, username, branchId)
    }
}