package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "transaction_items")
class TransactionItem : BaseEntity() {

    @Id
    @Column(name = "transaction_item_id", nullable = false)
    var transactionItemId: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    var transaction: Transaction = Transaction()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product = Product()

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 0

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    var subtotal: BigDecimal = BigDecimal.ZERO
}