package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class InventoryId(
    @Column(name = "ingredient_id")
    var ingredientId: Int = 0,

    @Column(name = "branch_id")
    var branchId: Int = 0
) : Serializable