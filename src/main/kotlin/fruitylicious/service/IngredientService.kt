package fruitylicious.service

import fruitylicious.config.DuplicateResourceException
import fruitylicious.dto.IngredientRequest
import fruitylicious.dto.IngredientResponse
import fruitylicious.entity.Ingredient
import fruitylicious.repository.local.LocalIngredientRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class IngredientService(
    private val localIngredientRepository: LocalIngredientRepository,
    private val auditService: AuditService
) {

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getAll(): List<IngredientResponse> =
        localIngredientRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(ingredientId: Long): IngredientResponse =
        localIngredientRepository.findById(ingredientId)
            .orElseThrow { EntityNotFoundException("Ingredient not found: $ingredientId") }
            .toResponse()

    @Transactional(readOnly = true)
    fun searchByName(name: String): List<IngredientResponse> =
        localIngredientRepository.findByIngredientNameContainingIgnoreCase(name)
            .map { it.toResponse() }

    /**
     * Internal lookup used by InventoryService and other services
     * that need the entity rather than the DTO.
     */
    @Transactional(readOnly = true)
    fun getEntityById(ingredientId: Long): Ingredient =
        localIngredientRepository.findById(ingredientId)
            .orElseThrow { EntityNotFoundException("Ingredient not found: $ingredientId") }

    // -------------------------------------------------------------------------
    // Writes
    // -------------------------------------------------------------------------

    @Transactional
    fun create(
        request: IngredientRequest,
        userId: Long,
        branchId: Long
    ): IngredientResponse {
        if (localIngredientRepository.existsByIngredientNameIgnoreCase(request.ingredientName)) {
            throw DuplicateResourceException(
                "Ingredient already exists: ${request.ingredientName}"
            )
        }

        val ingredient = Ingredient().apply {
            ingredientName         = request.ingredientName
            image                  = request.image
            unitType               = request.unitType
            estimatedWeightPerUnit = request.estimatedWeightPerUnit
            isPackaging            = request.isPackaging
            lastModified           = LocalDateTime.now()
            isSynced               = false
        }

        val saved = localIngredientRepository.save(ingredient)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.ADD_INGREDIENT,
            tableAffected = "ingredients",
            details       = "Created ingredient '${saved.ingredientName}' (id=${saved.ingredientId})"
        )

        return saved.toResponse()
    }

    @Transactional
    fun update(
        ingredientId: Long,
        request: IngredientRequest,
        userId: Long,
        branchId: Long
    ): IngredientResponse {
        val ingredient = localIngredientRepository.findById(ingredientId)
            .orElseThrow { EntityNotFoundException("Ingredient not found: $ingredientId") }

        val existing = localIngredientRepository
            .findByIngredientNameContainingIgnoreCase(request.ingredientName)
            .firstOrNull {
                it.ingredientId != ingredientId &&
                        it.ingredientName.equals(request.ingredientName, ignoreCase = true)
            }

        if (existing != null) {
            throw DuplicateResourceException(
                "Another ingredient already has name: ${request.ingredientName}"
            )
        }

        ingredient.apply {
            ingredientName         = request.ingredientName
            image                  = request.image
            unitType               = request.unitType
            estimatedWeightPerUnit = request.estimatedWeightPerUnit
            isPackaging            = request.isPackaging
            lastModified           = LocalDateTime.now()
            isSynced               = false
        }

        val saved = localIngredientRepository.save(ingredient)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.UPDATE_INGREDIENT,
            tableAffected = "ingredients",
            details       = "Updated ingredient '${saved.ingredientName}' (id=${saved.ingredientId})"
        )

        return saved.toResponse()
    }

    @Transactional
    fun delete(ingredientId: Long, userId: Long, branchId: Long) {
        val ingredient = localIngredientRepository.findById(ingredientId)
            .orElseThrow { EntityNotFoundException("Ingredient not found: $ingredientId") }

        localIngredientRepository.delete(ingredient)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.DELETE_INGREDIENT,
            tableAffected = "ingredients",
            details       = "Deleted ingredient '${ingredient.ingredientName}' (id=$ingredientId)"
        )
    }

    // -------------------------------------------------------------------------
    // Mapper
    // -------------------------------------------------------------------------

    fun Ingredient.toResponse() = IngredientResponse(
        ingredientId           = ingredientId,
        ingredientName         = ingredientName,
        image                  = image,
        unitType               = unitType,
        estimatedWeightPerUnit = estimatedWeightPerUnit,
        isPackaging            = isPackaging,
        lastModified           = lastModified,
        isSynced               = isSynced,
        syncedAt               = syncedAt
    )
}