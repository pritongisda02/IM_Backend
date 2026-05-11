package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "sync_conflicts",
    indexes = [
        Index(name = "idx_sync_conflicts_branch", columnList = "branch_id"),
        Index(name = "idx_sync_conflicts_table", columnList = "table_name"),
        Index(name = "idx_sync_conflicts_record", columnList = "record_id"),
        Index(name = "idx_sync_conflicts_created", columnList = "created_at")
    ]
)
open class SyncConflictEntity(
    @Id
    @Column(name = "conflict_id", length = 64)
    open var conflictId: String = "",

    @Column(name = "branch_id", nullable = false)
    open var branchId: Int = 0,

    @Column(name = "table_name", nullable = false)
    open var tableName: String = "",

    @Column(name = "record_id", nullable = false, length = 128)
    open var recordId: String = "",

    @Column(name = "reason", nullable = false, length = 1000)
    open var reason: String = "",

    @Column(name = "created_at", nullable = false)
    open var createdAt: Long = 0L
)