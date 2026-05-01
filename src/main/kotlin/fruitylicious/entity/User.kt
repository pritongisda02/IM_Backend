package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User : BaseEntity() {

    @Id
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0

    @Column(name = "name", nullable = false, length = 100)
    var name: String = ""

    @Column(name = "role", nullable = false, length = 20)
    var role: String = ""

    @Column(name = "username", nullable = false, unique = true, length = 50)
    var username: String = ""

    @Column(name = "password", nullable = false, length = 255)
    var password: String = ""
}