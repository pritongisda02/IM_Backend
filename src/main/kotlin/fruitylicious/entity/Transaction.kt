package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "transactions")
class Transaction : BaseEntity() {

    @Id
    @Column(name = "transaction_id", nullable = false)
    var transactionId: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = User()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    var branch: Branch = Branch()

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    var totalAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "payment_type", nullable = false, length = 20)
    var paymentType: String = ""

    @Column(name = "date_time", nullable = false)
    var dateTime: Instant = Instant.now()

    @Column(name = "status", nullable = false, length = 20)
    var status: String = "completed"
}