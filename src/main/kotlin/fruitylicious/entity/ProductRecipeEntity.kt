package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "product_recipes",
    indexes = [
        Index(name = "idx_product_recipes_product", columnList = "product_id"),
        Index(name = "idx_product_recipes_variant", columnList = "variant_id"),
        Index(name = "idx_product_recipes_ingredient", columnList = "ingredient_id")
    ]
)
open class ProductRecipeEntity(
    @Id
    @Column(name = "recipe_id")
    open var recipeId: Int = 0,

    @Column(name = "product_id", nullable = false)
    open var productId: Int = 0,

    @Column(name = "variant_id")
    open var variantId: Int? = null,

    @Column(name = "ingredient_id", nullable = false)
    open var ingredientId: Int = 0,

    @Column(name = "quantity_required", nullable = false)
    open var quantityRequired: Double = 0.0,

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null
)