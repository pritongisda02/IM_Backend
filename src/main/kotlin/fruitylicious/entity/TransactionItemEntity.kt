package fruitylicious.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "transaction_items",
    indexes = [
        Index(name = "idx_ti_txn", columnList = "transaction_id"),
        Index(name = "idx_ti_prod", columnList = "product_id"),
        Index(name = "idx_ti_var", columnList = "variant_id")
    ]
)
open class TransactionItemEntity(
    @Id
    @Column(name = "transaction_item_id", nullable = false)
    open var transactionItemId: String = "",

    @Column(name = "transaction_id", nullable = false)
    open var transactionId: String = "",

    @Column(name = "product_id", nullable = false)
    open var productId: Int = 0,

    @Column(name = "variant_id")
    open var variantId: Int? = null,

    @Column(name = "size_name")
    open var sizeName: String? = null,

    @Column(name = "quantity", nullable = false)
    open var quantity: Int = 1,

    @Column(name = "subtotal", nullable = false)
    open var subtotal: Double = 0.0,

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null
) {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "transaction_id",
        referencedColumnName = "transaction_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_items_transaction")
    )
    open var transaction: TransactionEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "product_id",
        referencedColumnName = "product_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_items_product")
    )
    open var product: ProductEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
        name = "variant_id",
        referencedColumnName = "variant_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_items_variant")
    )
    open var variant: ProductVariantEntity? = null
}