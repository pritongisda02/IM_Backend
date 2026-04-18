package fruitylicious.entity

import jakarta.persistence.*
@Entity
@Table(name = "TRANSACTION_ITEMS")
class TransactionItem(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_item_seq")
    @SequenceGenerator(name = "transaction_item_seq", sequenceName = "TRANSACTION_ITEM_SEQ", allocationSize = 1)
    @Column(name = "TRANSACTION_ITEM_ID")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID")
    val transaction: Transaction,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    val product: Product,

    val quantity: Int,

    val subtotal: Double
)