package fruitylicious.dto

import java.math.BigDecimal
import java.time.Instant

data class TransactionRequest(
    val transactionId: Long = 0,
    val userId: Long,
    val branchId: Long,
    val totalAmount: BigDecimal,
    val paymentType: String,
    val dateTime: Instant,
    val status: String = "completed"
)

data class TransactionResponse(
    val transactionId: Long,
    val userId: Long,
    val userName: String,
    val branchId: Long,
    val totalAmount: BigDecimal,
    val paymentType: String,
    val dateTime: Instant,
    val status: String,
    val items: List<TransactionItemResponse> = emptyList(),
    val lastModified: Instant
)