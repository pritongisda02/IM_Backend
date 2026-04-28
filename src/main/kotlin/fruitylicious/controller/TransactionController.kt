package fruitylicious.controller

import fruitylicious.config.JwtTokenProvider
import fruitylicious.dto.TransactionRequest
import fruitylicious.dto.TransactionResponse
import fruitylicious.service.TransactionService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionService: TransactionService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    // -------------------------------------------------------------------------
    // GET /api/transactions
    // Staff sees only their branch; admin sees all (no branchId filter here —
    // admin can query with a future ?branchId param if needed)
    // -------------------------------------------------------------------------

    @GetMapping
    fun getAll(
        httpRequest: HttpServletRequest
    ): ResponseEntity<List<TransactionResponse>> {
        val info     = resolveToken(httpRequest)
        val response = transactionService.getAll(info.branchId)
        return ResponseEntity.ok(response)
    }

    // -------------------------------------------------------------------------
    // GET /api/transactions/{id}
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        httpRequest: HttpServletRequest
    ): ResponseEntity<TransactionResponse> {
        val info     = resolveToken(httpRequest)
        val isAdmin  = info.role == "ADMIN"
        val response = transactionService.getById(id, info.branchId, isAdmin)
        return ResponseEntity.ok(response)
    }

    // -------------------------------------------------------------------------
    // POST /api/transactions
    // -------------------------------------------------------------------------

    @PostMapping
    fun create(
        @Valid @RequestBody request: TransactionRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<TransactionResponse> {
        val info     = resolveToken(httpRequest)
        val response = transactionService.create(request, info.userId, info.branchId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // -------------------------------------------------------------------------
    // PATCH /api/transactions/{id}/void
    // -------------------------------------------------------------------------

    @PatchMapping("/{id}/void")
    fun voidTransaction(
        @PathVariable id: Long,
        httpRequest: HttpServletRequest
    ): ResponseEntity<TransactionResponse> {
        val info     = resolveToken(httpRequest)
        val isAdmin  = info.role == "ADMIN"
        val response = transactionService.voidTransaction(
            transactionId = id,
            userId        = info.userId,
            branchId      = info.branchId,
            isAdmin       = isAdmin
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