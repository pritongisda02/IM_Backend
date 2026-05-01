package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "inventory_adjustments")
class InventoryAdjustment : BaseEntity() {

    @Id
    @Column(name = "adjustment_id", nullable = false)
    var adjustmentId: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    var ingredient: Ingredient = Ingredient()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    var branch: Branch = Branch()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = User()

    @Column(name = "adjustment_amount", nullable = false, precision = 10, scale = 4)
    var adjustmentAmount: BigDecimal = BigDecimal.ZERO

    @Column(name = "reason", length = 255)
    var reason: String? = null

    @Column(name = "date_time", nullable = false)
    var dateTime: Instant = Instant.now()
}