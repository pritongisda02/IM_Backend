package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "inventory_adjustments")
class InventoryAdjustment : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adjustment_id", nullable = false)
    var adjustmentId: Long = 0

    @Column(name = "ingredient_id", nullable = false)
    var ingredientId: Long = 0

    @Column(name = "branch_id", nullable = false)
    var branchId: Long = 0

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0

    @Column(name = "adjustment_amount", nullable = false, precision = 12, scale = 4)
    var adjustmentAmount: BigDecimal = BigDecimal.ZERO   // positive = add, negative = subtract

    @Column(name = "reason", nullable = false, length = 500)
    var reason: String = ""

    @Column(name = "date_time", nullable = false)
    var dateTime: LocalDateTime = LocalDateTime.now()
}