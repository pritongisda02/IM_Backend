package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0

    @Column(name = "name", nullable = false, length = 150)
    var name: String = ""

    @Column(name = "role", nullable = false, length = 10)
    var role: String = ""          // "admin" | "staff"

    @Column(name = "username", nullable = false, unique = true, length = 80)
    var username: String = ""

    @Column(name = "password", nullable = false, length = 255)
    var password: String = ""

    @Column(name = "branch_id")
    var branchId: Long? = null     // null means admin with no fixed branch
}