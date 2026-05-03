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
    name = "transaction_item_addons",
    indexes = [
        Index(name = "idx_tia_item", columnList = "transaction_item_id"),
        Index(name = "idx_tia_prod", columnList = "addon_product_id")
    ]
)
open class TransactionItemAddonEntity(
    @Id
    @Column(name = "transaction_item_addon_id", nullable = false)
    open var transactionItemAddonId: String = "",

    @Column(name = "transaction_item_id", nullable = false)
    open var transactionItemId: String = "",

    @Column(name = "addon_product_id", nullable = false)
    open var addonProductId: Int = 0,

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
        name = "transaction_item_id",
        referencedColumnName = "transaction_item_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_addons_item")
    )
    open var transactionItem: TransactionItemEntity? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "addon_product_id",
        referencedColumnName = "product_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_addons_product")
    )
    open var addonProduct: ProductEntity? = null
}