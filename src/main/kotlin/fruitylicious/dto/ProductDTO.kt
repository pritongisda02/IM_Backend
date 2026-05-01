package fruitylicious.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant

data class ProductRequest(
    val productId: Long = 0,
    val image: String? = null,

    @field:NotBlank(message = "Product name is required")
    val productName: String,

    val isAddon: Boolean = false,

    @field:NotNull(message = "Price is required")
    val price: BigDecimal
)

data class ProductResponse(
    val productId: Long,
    val image: String?,
    val productName: String,
    val isAddon: Boolean,
    val price: BigDecimal,
    val lastModified: Instant
)