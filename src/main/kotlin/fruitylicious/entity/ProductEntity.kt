package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(
    name = "products",
    indexes = [
        Index(name = "idx_products_name", columnList = "product_name"),
        Index(name = "idx_products_is_addon", columnList = "is_addon")
    ]
)
open class ProductEntity(
    @Id
    @Column(name = "product_id")
    open var productId: Int = 0,

    @Lob
    @Column(name = "image", columnDefinition = "CLOB")
    open var image: String? = null,

    @Column(name = "product_name", nullable = false)
    open var productName: String = "",

    @Column(name = "is_addon", nullable = false)
    open var isAddon: Boolean = false,

    @Column(name = "price", nullable = false)
    open var price: Double = 0.0,

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null,

    @Column(name = "is_deleted", nullable = false)
    open var isDeleted: Boolean = false,

    @Column(name = "deleted_at")
    open var deletedAt: Long? = null
)