package fruitylicious.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductRequest(
    @field:NotBlank(message = "Product name is required")
    val productName: String,

    val image: String? = null,

    @field:NotNull(message = "is_addon flag is required")
    val isAddon: Boolean,

    @field:NotNull(message = "Price is required")
    @field:DecimalMin(value = "0.00", message = "Price must be non-negative")
    val price: BigDecimal
)

data class ProductResponse(
    val productId: Long,
    val productName: String,
    val image: String?,
    val isAddon: Boolean,
    val price: BigDecimal,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)