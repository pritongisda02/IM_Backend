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
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "product_variants",
    indexes = [
        Index(name = "idx_product_variants_product", columnList = "product_id")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_product_variant_size",
            columnNames = ["product_id", "size_name"]
        )
    ]
)
open class ProductVariantEntity(
    @Id
    @Column(name = "variant_id", nullable = false)
    open var variantId: Int = 0,

    @Column(name = "product_id", nullable = false)
    open var productId: Int = 0,

    @Column(name = "size_name", nullable = false)
    open var sizeName: String = "",

    @Column(name = "price", nullable = false)
    open var price: Double = 0.0,

    @Column(name = "is_deleted", nullable = false)
    open var isDeleted: Boolean = false,

    @Column(name = "deleted_at")
    open var deletedAt: Long? = null,

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
        name = "product_id",
        referencedColumnName = "product_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_variants_product")
    )
    open var product: ProductEntity? = null
}