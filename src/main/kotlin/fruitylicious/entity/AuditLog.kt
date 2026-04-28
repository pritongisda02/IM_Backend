package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "audit_logs")
class AuditLog : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false)
    var logId: Long = 0

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0

    @Column(name = "branch_id", nullable = false)
    var branchId: Long = 0

    @Column(name = "action", nullable = false, length = 100)
    var action: String = ""        // e.g. "ADD_PRODUCT", "VOID_TRANSACTION"

    @Column(name = "table_affected", nullable = false, length = 100)
    var tableAffected: String = ""

    @Column(name = "timestamp", nullable = false)
    var timestamp: LocalDateTime = LocalDateTime.now()

    @Column(name = "details", length = 1000)
    var details: String? = null    // optional human-readable context
}