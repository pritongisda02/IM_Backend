package fruitylicious.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "INVENTORY_ADJUSTMENTS")
class InventoryAdjustment(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adjustment_seq")
    @SequenceGenerator(name = "adjustment_seq", sequenceName = "ADJUSTMENT_SEQ", allocationSize = 1)
    @Column(name = "ADJUSTMENT_ID")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INGREDIENT_ID")
    val ingredient: Ingredient,

    val adjustmentAmount: Double,

    val reason: String,

    val dateTime: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    val user: User
)