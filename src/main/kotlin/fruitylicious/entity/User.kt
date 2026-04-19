package fruitylicious.entity
import jakarta.persistence.*

@Entity
@Table(name = "USERS")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
    @Column(name = "USER_ID")
    var id: Long = 0,

    var name: String,

    @Column(unique = true)
    var username: String,

    var password: String,

    @Enumerated(EnumType.STRING)
    var role: UserRole
)

enum class UserRole {
    ADMIN, STAFF
}