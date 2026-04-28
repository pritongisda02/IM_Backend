package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "products")
class Product : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    var productId: Long = 0

    @Column(name = "image", length = 500)
    var image: String? = null      // URL or file path

    @Column(name = "product_name", nullable = false, length = 200)
    var productName: String = ""

    @Column(name = "is_addon", nullable = false)
    var isAddon: Boolean = false

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    var price: BigDecimal = BigDecimal.ZERO
}