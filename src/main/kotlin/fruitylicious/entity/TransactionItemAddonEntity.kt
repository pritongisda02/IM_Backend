package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "transaction_item_addons",
    indexes = [
        Index(name = "idx_tia_item", columnList = "transaction_item_id"),
        Index(name = "idx_tia_prod", columnList = "addon_product_id")
    ]
)
open class TransactionItemAddonEntity(
    @Id
    @Column(name = "transaction_item_addon_id", length = 64)
    open var transactionItemAddonId: String = "",

    @Column(name = "transaction_item_id", nullable = false, length = 64)
    open var transactionItemId: String = "",

    @Column(name = "addon_product_id", nullable = false)
    open var addonProductId: Int = 0,

    @Column(name = "quantity", nullable = false)
    open var quantity: Int = 0,

    @Column(name = "subtotal", nullable = false)
    open var subtotal: Double = 0.0,

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null
)