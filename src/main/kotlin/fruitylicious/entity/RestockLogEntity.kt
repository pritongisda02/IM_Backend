package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "restock_logs",
    indexes = [
        Index(name = "idx_restock_ingredient", columnList = "ingredient_id"),
        Index(name = "idx_restock_branch", columnList = "branch_id"),
        Index(name = "idx_restock_user", columnList = "user_id"),
        Index(name = "idx_restock_date", columnList = "date_time")
    ]
)
open class RestockLogEntity(
    @Id
    @Column(name = "restock_id", length = 64)
    open var restockId: String = "",

    @Column(name = "ingredient_id", nullable = false)
    open var ingredientId: Int = 0,

    @Column(name = "branch_id", nullable = false)
    open var branchId: Int = 0,

    @Column(name = "user_id", nullable = false)
    open var userId: Int = 0,

    @Column(name = "quantity_added", nullable = false)
    open var quantityAdded: Double = 0.0,

    @Column(name = "supplier", nullable = false)
    open var supplier: String = "",

    @Column(name = "date_time", nullable = false)
    open var dateTime: Long = 0L,

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null
)