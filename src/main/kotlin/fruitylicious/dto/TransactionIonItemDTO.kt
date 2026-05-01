package fruitylicious.dto

import java.math.BigDecimal
import java.time.Instant

data class TransactionItemRequest(
    val transactionItemId: Long = 0,
    val transactionId: Long,
    val productId: Long,
    val quantity: Int,
    val subtotal: BigDecimal
)

data class TransactionItemResponse(
    val transactionItemId: Long,
    val transactionId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val subtotal: BigDecimal,
    val lastModified: Instant
)