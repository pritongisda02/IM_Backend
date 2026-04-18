package fruitylicious.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "WASTE_LOGS")
class WasteLog(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "waste_seq")
    @SequenceGenerator(name = "waste_seq", sequenceName = "WASTE_SEQ", allocationSize = 1)
    @Column(name = "WASTE_ID")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INGREDIENT_ID")
    val ingredient: Ingredient,

    val quantity: Double,

    val reason: String,

    val dateTime: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    val user: User
)