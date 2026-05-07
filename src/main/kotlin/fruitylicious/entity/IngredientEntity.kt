package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(
    name = "ingredients",
    indexes = [
        Index(name = "idx_ingredients_name", columnList = "ingredient_name"),
        Index(name = "idx_ingredients_packaging", columnList = "is_packaging")
    ]
)
open class IngredientEntity(
    @Id
    @Column(name = "ingredient_id", nullable = false)
    open var ingredientId: Int = 0,

    @Lob
    @Column(name = "image", columnDefinition = "CLOB")
    open var image: String? = null,

    @Column(name = "ingredient_name", nullable = false, unique = true)
    open var ingredientName: String = "",

    @Column(name = "unit_type", nullable = false)
    open var unitType: String = "",

    @Column(name = "is_packaging", nullable = false)
    open var isPackaging: Boolean = false,

    @Column(name = "low_stock_threshold", nullable = false)
    open var lowStockThreshold: Double = 0.0,

    @Column(name = "is_deleted", nullable = false)
    open var isDeleted: Boolean = false,

    @Column(name = "deleted_at")
    open var deletedAt: Long? = null,

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null
)