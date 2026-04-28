package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.math.BigDecimal

data class InventoryId(
    val ingredientId: Long = 0,
    val branchId: Long = 0
) : Serializable

@Entity
@Table(name = "inventory")
@IdClass(InventoryId::class)
class Inventory : BaseEntity() {

    @Id
    @Column(name = "ingredient_id", nullable = false)
    var ingredientId: Long = 0

    @Id
    @Column(name = "branch_id", nullable = false)
    var branchId: Long = 0

    @Column(name = "current_stock", nullable = false, precision = 12, scale = 4)
    var currentStock: BigDecimal = BigDecimal.ZERO
}