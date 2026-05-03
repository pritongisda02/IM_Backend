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
    @Column(name = "restock_id", nullable = false)
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
    open var supplier: String = "N/A",

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
        foreignKey = ForeignKey(name = "fk_restock_ingredient")
    )
    open var ingredient: IngredientEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "branch_id",
        referencedColumnName = "branch_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_restock_branch")
    )
    open var branch: BranchEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "user_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_restock_user")
    )
    open var user: UserEntity? = null
}