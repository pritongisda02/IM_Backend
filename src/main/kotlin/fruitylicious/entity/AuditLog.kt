package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "audit_logs")
class AuditLog : BaseEntity() {

    @Id
    @Column(name = "log_id", nullable = false)
    var logId: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity = UserEntity()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    var branch: Branch = Branch()

    @Column(name = "action", nullable = false, length = 255)
    var action: String = ""

    @Column(name = "table_affected", nullable = false, length = 100)
    var tableAffected: String = ""

    @Column(name = "timestamp", nullable = false)
    var timestamp: Instant = Instant.now()
}