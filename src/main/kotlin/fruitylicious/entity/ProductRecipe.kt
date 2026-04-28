package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "product_recipes")
class ProductRecipe : BaseEntity() {

    @Id
    @Column(name = "recipe_id", nullable = false)
    var recipeId: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product = Product()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    var ingredient: Ingredient = Ingredient()

    @Column(name = "quantity_required", nullable = false, precision = 10, scale = 4)
    var quantityRequired: BigDecimal = BigDecimal.ZERO
}