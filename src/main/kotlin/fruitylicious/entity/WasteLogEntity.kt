package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(
    name = "waste_logs",
    indexes = [
        Index(name = "idx_waste_ingredient", columnList = "ingredient_id"),
        Index(name = "idx_waste_branch", columnList = "branch_id"),
        Index(name = "idx_waste_user", columnList = "user_id"),
        Index(name = "idx_waste_date", columnList = "date_time")
    ]
)
open class WasteLogEntity(
    @Id
    @Column(name = "waste_id", length = 64)
    open var wasteId: String = "",

    @Column(name = "ingredient_id", nullable = false)
    open var ingredientId: Int = 0,

    @Column(name = "branch_id", nullable = false)
    open var branchId: Int = 0,

    @Column(name = "user_id", nullable = false)
    open var userId: Int = 0,

    @Column(name = "quantity", nullable = false)
    open var quantity: Double = 0.0,

    @Lob
    @Column(name = "image", columnDefinition = "CLOB")
    open var image: String? = null,

    @Column(name = "reason", nullable = false)
    open var reason: String = "",

    @Column(name = "date_time", nullable = false)
    open var dateTime: Long = 0L,

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null
)