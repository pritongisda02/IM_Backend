package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "transaction_items",
    indexes = [
        Index(name = "idx_transaction_items_transaction", columnList = "transaction_id"),
        Index(name = "idx_transaction_items_product", columnList = "product_id"),
        Index(name = "idx_transaction_items_variant", columnList = "variant_id")
    ]
)
open class TransactionItemEntity(
    @Id
    @Column(name = "transaction_item_id", length = 64)
    open var transactionItemId: String = "",

    @Column(name = "transaction_id", nullable = false, length = 64)
    open var transactionId: String = "",

    @Column(name = "product_id", nullable = false)
    open var productId: Int = 0,

    @Column(name = "variant_id")
    open var variantId: Int? = null,

    @Column(name = "size_name")
    open var sizeName: String? = null,

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