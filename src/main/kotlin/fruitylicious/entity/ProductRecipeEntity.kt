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
    name = "product_recipes",
    indexes = [
        Index(name = "idx_product_recipes_product", columnList = "product_id"),
        Index(name = "idx_product_recipes_variant", columnList = "variant_id"),
        Index(name = "idx_product_recipes_ingredient", columnList = "ingredient_id")
    ]
)
open class ProductRecipeEntity(
    @Id
    @Column(name = "recipe_id", nullable = false)
    open var recipeId: Int = 0,

    @Column(name = "product_id", nullable = false)
    open var productId: Int = 0,

    @Column(name = "variant_id")
    open var variantId: Int? = null,

    @Column(name = "ingredient_id", nullable = false)
    open var ingredientId: Int = 0,

    @Column(name = "quantity_required", nullable = false)
    open var quantityRequired: Double = 0.0,

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
) {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "product_id",
        referencedColumnName = "product_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_recipes_product")
    )
    open var product: ProductEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
        name = "variant_id",
        referencedColumnName = "variant_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_recipes_variant")
    )
    open var variant: ProductVariantEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "ingredient_id",
        referencedColumnName = "ingredient_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_recipes_ingredient")
    )
    open var ingredient: IngredientEntity? = null
}