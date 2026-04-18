package fruitylicious.entity

import jakarta.persistence.*
@Entity
@Table(name = "INVENTORY")
class   Inventory(

    @Id
    @Column(name = "INGREDIENT_ID")
    val ingredientId: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "INGREDIENT_ID")
    val ingredient: Ingredient,

    val currentStock: Double
)