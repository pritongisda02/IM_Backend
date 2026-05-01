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
@Table(name = "restock_logs")
class RestockLog : BaseEntity() {

    @Id
    @Column(name = "restock_id", nullable = false)
    var restockId: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    var ingredient: Ingredient = Ingredient()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    var branch: Branch = Branch()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity = UserEntity()

    @Column(name = "quantity_added", nullable = false, precision = 10, scale = 4)
    var quantityAdded: BigDecimal = BigDecimal.ZERO

    @Column(name = "supplier", length = 100)
    var supplier: String? = null

    @Column(name = "date_time", nullable = false)
    var dateTime: Instant = Instant.now()
}