package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
open class InventoryId(
    @Column(name = "ingredient_id")
    open var ingredientId: Int = 0,

    @Column(name = "branch_id")
    open var branchId: Int = 0
) : Serializable