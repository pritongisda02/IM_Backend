package fruitylicious.entity

import jakarta.persistence.*
@Entity
@Table(name = "INGREDIENTS")
class Ingredient(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingredient_seq")
    @SequenceGenerator(name = "ingredient_seq", sequenceName = "INGREDIENT_SEQ", allocationSize = 1)
    @Column(name = "INGREDIENT_ID")
    val id: Long = 0,

    var name: String,

    @Enumerated(EnumType.STRING)
    var unitType: UnitType,

    var estimatedWeightPerUnit: Double? = null,

    var isPackaging: Boolean
)

enum class UnitType {
    PCS, G, ML, CAN, PACK
}