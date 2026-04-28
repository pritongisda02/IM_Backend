package fruitylicious.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

// --- Create transaction ---

data class TransactionItemRequest(
    @field:NotNull(message = "Product ID is required")
    val productId: Long,

    @field:NotNull(message = "Quantity is required")
    @field:Positive(message = "Quantity must be positive")
    val quantity: Int
)

data class TransactionRequest(
    @field:NotEmpty(message = "At least one item is required")
    val items: List<TransactionItemRequest>,

    @field:NotBlank(message = "Payment type is required")
    val paymentType: String
)

// --- Responses ---

data class TransactionItemResponse(
    val transactionItemId: Long,
    val transactionId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val subtotal: BigDecimal,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)

data class TransactionResponse(
    val transactionId: Long,
    val userId: Long,
    val branchId: Long,
    val totalAmount: BigDecimal,
    val paymentType: String,
    val dateTime: LocalDateTime,
    val status: String,
    val items: List<TransactionItemResponse>,
    val lastModified: LocalDateTime,
    val isSynced: Boolean,
    val syncedAt: LocalDateTime?
)