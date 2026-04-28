package fruitylicious.service

import fruitylicious.config.DuplicateResourceException
import fruitylicious.dto.RecipeRequest
import fruitylicious.dto.RecipeResponse
import fruitylicious.dto.RecipeUpdateRequest
import fruitylicious.entity.ProductRecipe
import fruitylicious.repository.local.LocalIngredientRepository
import fruitylicious.repository.local.LocalProductRecipeRepository
import fruitylicious.repository.local.LocalProductRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RecipeService(
    private val localProductRecipeRepository: LocalProductRecipeRepository,
    private val localProductRepository: LocalProductRepository,
    private val localIngredientRepository: LocalIngredientRepository,
    private val auditService: AuditService
) {

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getByProduct(productId: Long): List<RecipeResponse> {
        // Validate product exists
        localProductRepository.findById(productId)
            .orElseThrow { EntityNotFoundException("Product not found: $productId") }

        return localProductRecipeRepository
            .findAllByProductId(productId)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getById(recipeId: Long): RecipeResponse =
        localProductRecipeRepository.findById(recipeId)
            .orElseThrow { EntityNotFoundException("Recipe not found: $recipeId") }
            .toResponse()

    // -------------------------------------------------------------------------
    // Writes
    // -------------------------------------------------------------------------

    @Transactional
    fun create(
        request: RecipeRequest,
        userId: Long,
        branchId: Long
    ): RecipeResponse {
        // Validate product exists
        localProductRepository.findById(request.productId)
            .orElseThrow { EntityNotFoundException("Product not found: ${request.productId}") }

        // Validate ingredient exists
        localIngredientRepository.findById(request.ingredientId)
            .orElseThrow { EntityNotFoundException("Ingredient not found: ${request.ingredientId}") }

        // No duplicate recipe entries for same product + ingredient
        if (localProductRecipeRepository.existsByProductIdAndIngredientId(
                request.productId,
                request.ingredientId
            )
        ) {
            throw DuplicateResourceException(
                "Recipe entry already exists for product ${request.productId} " +
                        "and ingredient ${request.ingredientId}"
            )
        }

        val recipe = ProductRecipe().apply {
            productId        = request.productId
            ingredientId     = request.ingredientId
            quantityRequired = request.quantityRequired
            lastModified     = LocalDateTime.now()
            isSynced         = false
        }

        val saved = localProductRecipeRepository.save(recipe)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.ADD_RECIPE,
            tableAffected = "product_recipes",
            details       = "Added recipe entry (id=${saved.recipeId}) for " +
                    "product=${request.productId}, ingredient=${request.ingredientId}"
        )

        return saved.toResponse()
    }

    @Transactional
    fun update(
        recipeId: Long,
        request: RecipeUpdateRequest,
        userId: Long,
        branchId: Long
    ): RecipeResponse {
        val recipe = localProductRecipeRepository.findById(recipeId)
            .orElseThrow { EntityNotFoundException("Recipe not found: $recipeId") }

        recipe.apply {
            quantityRequired = request.quantityRequired
            lastModified     = LocalDateTime.now()
            isSynced         = false
        }

        val saved = localProductRecipeRepository.save(recipe)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.UPDATE_RECIPE,
            tableAffected = "product_recipes",
            details       = "Updated recipe (id=$recipeId) quantityRequired=${request.quantityRequired}"
        )

        return saved.toResponse()
    }

    @Transactional
    fun delete(recipeId: Long, userId: Long, branchId: Long) {
        val recipe = localProductRecipeRepository.findById(recipeId)
            .orElseThrow { EntityNotFoundException("Recipe not found: $recipeId") }

        localProductRecipeRepository.delete(recipe)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.DELETE_RECIPE,
            tableAffected = "product_recipes",
            details       = "Deleted recipe (id=$recipeId) for product=${recipe.productId}, " +
                    "ingredient=${recipe.ingredientId}"
        )
    }

    // -------------------------------------------------------------------------
    // Internal helper — used by TransactionService for stock deduction
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getRecipesForProduct(productId: Long): List<ProductRecipe> =
        localProductRecipeRepository.findAllByProductId(productId)

    // -------------------------------------------------------------------------
    // Mapper
    // -------------------------------------------------------------------------

    private fun ProductRecipe.toResponse(): RecipeResponse {
        val ingredient = localIngredientRepository.findById(ingredientId)
            .orElse(null)

        return RecipeResponse(
            recipeId         = recipeId,
            productId        = productId,
            ingredientId     = ingredientId,
            ingredientName   = ingredient?.ingredientName ?: "Unknown",
            unitType         = ingredient?.unitType ?: "",
            quantityRequired = quantityRequired,
            lastModified     = lastModified,
            isSynced         = isSynced,
            syncedAt         = syncedAt
        )
    }
}