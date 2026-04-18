package fruitylicious.entity

import jakarta.persistence.*
@Entity
@Table(name = "PRODUCTS")
class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    @Column(name = "PRODUCT_ID")
    val id: Long = 0,

    var name: String,

    var price: Double,

    @Column(name = "IS_ADDON")
    var isAddon: Boolean
)