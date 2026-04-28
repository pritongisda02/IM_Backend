package fruitylicious.entity

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class InventoryId(
    val ingredientId: Long = 0,
    val branchId: Long = 0
) : Serializable