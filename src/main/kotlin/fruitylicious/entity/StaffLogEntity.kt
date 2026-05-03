package fruitylicious.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "staff_logs",
    indexes = [
        Index(name = "idx_staff_logs_user", columnList = "user_id"),
        Index(name = "idx_staff_logs_branch", columnList = "branch_id"),
        Index(name = "idx_staff_logs_clock_in", columnList = "clock_in"),
        Index(name = "idx_staff_logs_clock_out", columnList = "clock_out")
    ]
)
open class StaffLogEntity(
    @Id
    @Column(name = "log_id", nullable = false)
    open var logId: String = "",

    @Column(name = "user_id", nullable = false)
    open var userId: Int = 0,

    @Column(name = "branch_id", nullable = false)
    open var branchId: Int = 0,

    @Lob
    @Column(name = "image", columnDefinition = "CLOB")
    open var image: String? = null,

    @Column(name = "clock_in", nullable = false)
    open var clockIn: Long = 0L,

    @Column(name = "clock_out")
    open var clockOut: Long? = null,

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
        foreignKey = ForeignKey(name = "fk_staff_logs_user")
    )
    open var user: UserEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "branch_id",
        referencedColumnName = "branch_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_staff_logs_branch")
    )
    open var branch: BranchEntity? = null
}