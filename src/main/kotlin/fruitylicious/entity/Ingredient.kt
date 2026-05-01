package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "ingredients")
class Ingredient : BaseEntity() {

    @Id
    @Column(name = "ingredient_id", nullable = false)
    var ingredientId: Long = 0

    @Column(name = "image", length = 500)
    var image: String? = null

    @Column(name = "ingredient_name", nullable = false, length = 100)
    var ingredientName: String = ""

    @Column(name = "unit_type", nullable = false, length = 20)
    var unitType: String = ""

    @Column(name = "estimated_weight_per_unit", precision = 10, scale = 4)
    var estimatedWeightPerUnit: BigDecimal? = null

    @Column(name = "is_packaging", nullable = false)
    var isPackaging: Boolean = false

    @Column(name = "low_stock_threshold", nullable = false, precision = 10, scale = 4)
    var lowStockThreshold: BigDecimal = BigDecimal.ZERO
}