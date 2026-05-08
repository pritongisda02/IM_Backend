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
    name = "transactions",
    indexes = [
        Index(name = "idx_txn_user", columnList = "user_id"),
        Index(name = "idx_txn_branch", columnList = "branch_id"),
        Index(name = "idx_txn_date", columnList = "date_time"),
        Index(name = "idx_txn_status", columnList = "status"),
        Index(name = "idx_txn_branch_date", columnList = "branch_id,date_time")
    ]
)
open class TransactionEntity(
    @Id
    @Column(name = "transaction_id", nullable = false)
    open var transactionId: String = "",

    @Column(name = "user_id", nullable = false)
    open var userId: Int = 0,

    @Column(name = "branch_id", nullable = false)
    open var branchId: Int = 0,

    @Column(name = "transaction_name")
    open var transactionName: String? = null,

    @Column(name = "total_amount", nullable = false)
    open var totalAmount: Double = 0.0,

    @Column(name = "payment_type", nullable = false)
    open var paymentType: String = "Cash",

    @Column(name = "date_time", nullable = false)
    open var dateTime: Long = 0L,

    @Column(name = "status", nullable = false)
    open var status: String = "pending",

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
        foreignKey = ForeignKey(name = "fk_transactions_user")
    )
    open var user: UserEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "branch_id",
        referencedColumnName = "branch_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_transactions_branch")
    )
    open var branch: BranchEntity? = null
}