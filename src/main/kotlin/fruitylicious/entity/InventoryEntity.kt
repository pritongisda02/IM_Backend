package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "inventory",
    indexes = [
        Index(name = "idx_inventory_ingredient", columnList = "ingredient_id"),
        Index(name = "idx_inventory_branch", columnList = "branch_id")
    ]
)
open class InventoryEntity(
    @EmbeddedId
    open var id: InventoryId = InventoryId(),

    @Column(name = "current_stock", nullable = false)
    open var currentStock: Double = 0.0,

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null
) {
    val ingredientId: Int
        get() = id.ingredientId

    val branchId: Int
        get() = id.branchId
}