package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
class Transaction : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    var transactionId: Long = 0

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0

    @Column(name = "branch_id", nullable = false)
    var branchId: Long = 0

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    var totalAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "payment_type", nullable = false, length = 50)
    var paymentType: String = ""   // e.g. "cash", "gcash", "card"

    @Column(name = "date_time", nullable = false)
    var dateTime: LocalDateTime = LocalDateTime.now()

    @Column(name = "status", nullable = false, length = 20)
    var status: String = "completed"   // "completed" | "void"
}