package fruitylicious.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.Transient

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

    @get:Transient
    @get:JsonProperty("ingredientId")
    @set:JsonProperty("ingredientId")
    open var ingredientId: Int
        get() = id.ingredientId
        set(value) {
            id.ingredientId = value
        }

    @get:Transient
    @get:JsonProperty("branchId")
    @set:JsonProperty("branchId")
    open var branchId: Int
        get() = id.branchId
        set(value) {
            id.branchId = value
        }
}