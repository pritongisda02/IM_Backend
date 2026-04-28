package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "ingredients")
class Ingredient : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id", nullable = false)
    var ingredientId: Long = 0

    @Column(name = "image", length = 500)
    var image: String? = null      // URL or file path

    @Column(name = "ingredient_name", nullable = false, length = 200)
    var ingredientName: String = ""

    @Column(name = "unit_type", nullable = false, length = 50)
    var unitType: String = ""      // e.g. "ml", "g", "pcs"

    @Column(name = "estimated_weight_per_unit", precision = 10, scale = 4)
    var estimatedWeightPerUnit: BigDecimal? = null

    @Column(name = "is_packaging", nullable = false)
    var isPackaging: Boolean = false
}