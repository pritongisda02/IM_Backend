package fruitylicious.service

import fruitylicious.config.DuplicateResourceException
import fruitylicious.dto.ProductRequest
import fruitylicious.dto.ProductResponse
import fruitylicious.entity.Product
import fruitylicious.repository.local.LocalProductRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProductService(
    private val localProductRepository: LocalProductRepository,
    private val auditService: AuditService
) {

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getAll(): List<ProductResponse> =
        localProductRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(productId: Long): ProductResponse =
        localProductRepository.findById(productId)
            .orElseThrow { EntityNotFoundException("Product not found: $productId") }
            .toResponse()

    @Transactional(readOnly = true)
    fun searchByName(name: String): List<ProductResponse> =
        localProductRepository.findByProductNameContainingIgnoreCase(name)
            .map { it.toResponse() }

    // -------------------------------------------------------------------------
    // Writes
    // -------------------------------------------------------------------------

    @Transactional
    fun create(request: ProductRequest, userId: Long, branchId: Long): ProductResponse {
        if (localProductRepository.existsByProductNameIgnoreCase(request.productName)) {
            throw DuplicateResourceException(
                "Product already exists: ${request.productName}"
            )
        }

        val product = Product().apply {
            productName  = request.productName
            image        = request.image
            isAddon      = request.isAddon
            price        = request.price
            lastModified = LocalDateTime.now()
            isSynced     = false
        }

        val saved = localProductRepository.save(product)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.ADD_PRODUCT,
            tableAffected = "products",
            details       = "Created product '${saved.productName}' (id=${saved.productId})"
        )

        return saved.toResponse()
    }

    @Transactional
    fun update(
        productId: Long,
        request: ProductRequest,
        userId: Long,
        branchId: Long
    ): ProductResponse {
        val product = localProductRepository.findById(productId)
            .orElseThrow { EntityNotFoundException("Product not found: $productId") }

        // Allow same name on same record; block duplicate on different record
        val existing = localProductRepository
            .findByProductNameContainingIgnoreCase(request.productName)
            .firstOrNull { it.productId != productId && it.productName.equals(request.productName, ignoreCase = true) }

        if (existing != null) {
            throw DuplicateResourceException(
                "Another product already has name: ${request.productName}"
            )
        }

        product.apply {
            productName  = request.productName
            image        = request.image
            isAddon      = request.isAddon
            price        = request.price
            lastModified = LocalDateTime.now()
            isSynced     = false
        }

        val saved = localProductRepository.save(product)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.UPDATE_PRODUCT,
            tableAffected = "products",
            details       = "Updated product '${saved.productName}' (id=${saved.productId})"
        )

        return saved.toResponse()
    }

    @Transactional
    fun delete(productId: Long, userId: Long, branchId: Long) {
        val product = localProductRepository.findById(productId)
            .orElseThrow { EntityNotFoundException("Product not found: $productId") }

        localProductRepository.delete(product)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.DELETE_PRODUCT,
            tableAffected = "products",
            details       = "Deleted product '${product.productName}' (id=$productId)"
        )
    }

    // -------------------------------------------------------------------------
    // Mapper
    // -------------------------------------------------------------------------

    fun Product.toResponse() = ProductResponse(
        productId    = productId,
        productName  = productName,
        image        = image,
        isAddon      = isAddon,
        price        = price,
        lastModified = lastModified,
        isSynced     = isSynced,
        syncedAt     = syncedAt
    )
}