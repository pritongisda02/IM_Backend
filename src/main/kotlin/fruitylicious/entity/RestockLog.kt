package fruitylicious.entity

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "RESTOCK_LOGS")
class RestockLog(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restock_seq")
    @SequenceGenerator(name = "restock_seq", sequenceName = "RESTOCK_SEQ", allocationSize = 1)
    @Column(name = "RESTOCK_ID")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INGREDIENT_ID")
    val ingredient: Ingredient,

    val quantityAdded: Double,

    val dateTime: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    val user: User
)