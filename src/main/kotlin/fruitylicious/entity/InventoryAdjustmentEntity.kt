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
    name = "inventory_adjustments",
    indexes = [
        Index(name = "idx_adjustments_ingredient", columnList = "ingredient_id"),
        Index(name = "idx_adjustments_branch", columnList = "branch_id"),
        Index(name = "idx_adjustments_user", columnList = "user_id"),
        Index(name = "idx_adjustments_date", columnList = "date_time")
    ]
)
open class InventoryAdjustmentEntity(
    @Id
    @Column(name = "adjustment_id", nullable = false)
    open var adjustmentId: String = "",

    @Column(name = "ingredient_id", nullable = false)
    open var ingredientId: Int = 0,

    @Column(name = "branch_id", nullable = false)
    open var branchId: Int = 0,

    @Column(name = "user_id", nullable = false)
    open var userId: Int = 0,

    @Column(name = "adjustment_amount", nullable = false)
    open var adjustmentAmount: Double = 0.0,

    @Column(name = "reason", nullable = false)
    open var reason: String = "Inventory adjustment",

    @Column(name = "date_time", nullable = false)
    open var dateTime: Long = 0L,

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
        name = "ingredient_id",
        referencedColumnName = "ingredient_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_adjustments_ingredient")
    )
    open var ingredient: IngredientEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "branch_id",
        referencedColumnName = "branch_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_adjustments_branch")
    )
    open var branch: BranchEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "user_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_adjustments_user")
    )
    open var user: UserEntity? = null
}