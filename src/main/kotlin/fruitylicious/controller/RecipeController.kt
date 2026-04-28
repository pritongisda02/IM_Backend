package fruitylicious.controller

import fruitylicious.config.JwtTokenProvider
import fruitylicious.dto.RecipeRequest
import fruitylicious.dto.RecipeResponse
import fruitylicious.dto.RecipeUpdateRequest
import fruitylicious.service.RecipeService
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/recipes")
@PreAuthorize("hasRole('ADMIN')")
class RecipeController(
    private val recipeService: RecipeService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @GetMapping("/{productId}")
    fun getByProduct(
        @PathVariable productId: Long
    ): ResponseEntity<List<RecipeResponse>> =
        ResponseEntity.ok(recipeService.getByProduct(productId))

    @PostMapping
    fun create(
        @Valid @RequestBody request: RecipeRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<RecipeResponse> {
        val (userId, branchId) = resolveUser(httpRequest)
        val response = recipeService.create(request, userId, branchId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{recipeId}")
    fun update(
        @PathVariable recipeId: Long,
        @Valid @RequestBody request: RecipeUpdateRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<RecipeResponse> {
        val (userId, branchId) = resolveUser(httpRequest)
        val response = recipeService.update(recipeId, request, userId, branchId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{recipeId}")
    fun delete(
        @PathVariable recipeId: Long,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        val (userId, branchId) = resolveUser(httpRequest)
        recipeService.delete(recipeId, userId, branchId)
        return ResponseEntity.ok(mapOf("message" to "Recipe $recipeId deleted successfully"))
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private fun resolveUser(request: HttpServletRequest): Pair<Long, Long> {
        val token    = request.getHeader("Authorization").substring(7)
        val userId   = jwtTokenProvider.getUserIdFromToken(token)
        val branchId = jwtTokenProvider.getBranchIdFromToken(token) ?: 0L
        return Pair(userId, branchId)
    }
}