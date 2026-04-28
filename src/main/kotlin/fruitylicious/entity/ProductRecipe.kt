package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "product_recipes")
class ProductRecipe : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id", nullable = false)
    var recipeId: Long = 0

    @Column(name = "product_id", nullable = false)
    var productId: Long = 0

    @Column(name = "ingredient_id", nullable = false)
    var ingredientId: Long = 0

    @Column(name = "quantity_required", nullable = false, precision = 10, scale = 4)
    var quantityRequired: BigDecimal = BigDecimal.ZERO
}