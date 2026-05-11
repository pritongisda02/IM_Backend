package fruitylicious.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "audit_logs",
    indexes = [
        Index(name = "idx_audit_user", columnList = "user_id"),
        Index(name = "idx_audit_branch", columnList = "branch_id"),
        Index(name = "idx_audit_action", columnList = "action"),
        Index(name = "idx_audit_table", columnList = "table_affected"),
        Index(name = "idx_audit_timestamp", columnList = "timestamp")
    ]
)
open class AuditLogEntity(
    @Id
    @Column(name = "log_id", nullable = false)
    open var logId: String = "",

    @Column(name = "user_id", nullable = false)
    open var userId: Int = 0,

    @Column(name = "branch_id", nullable = false)
    open var branchId: Int = 0,

    @Column(name = "action", nullable = false)
    open var action: String = "",

    @Column(name = "table_affected", nullable = false)
    open var tableAffected: String = "",

    @Column(name = "timestamp", nullable = false)
    open var timestamp: Long = 0L,

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null
) {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "user_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_audit_user")
    )
    open var user: UserEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "branch_id",
        referencedColumnName = "branch_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_audit_branch")
    )
    open var branch: BranchEntity? = null
}