package fruitylicious.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "TRANSACTIONS")
class Transaction(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @SequenceGenerator(name = "transaction_seq", sequenceName = "TRANSACTION_SEQ", allocationSize = 1)
    @Column(name = "TRANSACTION_ID")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    val user: User,

    val totalAmount: Double,

    @Enumerated(EnumType.STRING)
    val paymentType: PaymentType,

    val dateTime: LocalDateTime,

    @Enumerated(EnumType.STRING)
    val status: TransactionStatus
)

enum class PaymentType {
    CASH, ONLINE
}

enum class TransactionStatus {
    COMPLETED, VOID
}