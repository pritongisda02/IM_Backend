package fruitylicious.controller

import fruitylicious.config.JwtTokenProvider
import fruitylicious.dto.ProductRequest
import fruitylicious.dto.ProductResponse
import fruitylicious.service.ProductService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    // -------------------------------------------------------------------------
    // Staff + Admin
    // -------------------------------------------------------------------------

    @GetMapping
    fun getAll(): ResponseEntity<List<ProductResponse>> =
        ResponseEntity.ok(productService.getAll())

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long
    ): ResponseEntity<ProductResponse> =
        ResponseEntity.ok(productService.getById(id))

    @GetMapping("/search")
    fun search(
        @RequestParam name: String
    ): ResponseEntity<List<ProductResponse>> =
        ResponseEntity.ok(productService.searchByName(name))

    // -------------------------------------------------------------------------
    // Admin Only
    // -------------------------------------------------------------------------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(
        @Valid @RequestBody request: ProductRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ProductResponse> {
        val (userId, branchId) = resolveUser(httpRequest)
        val response = productService.create(request, userId, branchId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: ProductRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ProductResponse> {
        val (userId, branchId) = resolveUser(httpRequest)
        val response = productService.update(id, request, userId, branchId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(
        @PathVariable id: Long,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        val (userId, branchId) = resolveUser(httpRequest)
        productService.delete(id, userId, branchId)
        return ResponseEntity.ok(mapOf("message" to "Product $id deleted successfully"))
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private fun resolveUser(request: HttpServletRequest): Pair<Long, Long> {
        val token   = request.getHeader("Authorization").substring(7)
        val userId  = jwtTokenProvider.getUserIdFromToken(token)
        val branchId = jwtTokenProvider.getBranchIdFromToken(token) ?: 0L
        return Pair(userId, branchId)
    }
}