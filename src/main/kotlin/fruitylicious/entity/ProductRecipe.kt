package fruitylicious.entity

import jakarta.persistence.*

@Entity
@Table(name = "PRODUCT_RECIPES")
class ProductRecipe(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipe_seq")
    @SequenceGenerator(name = "recipe_seq", sequenceName = "RECIPE_SEQ", allocationSize = 1)
    @Column(name = "RECIPE_ID")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INGREDIENT_ID")
    val ingredient: Ingredient,

    val quantityRequired: Double
)