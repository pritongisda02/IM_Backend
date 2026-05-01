package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_users_username", columnList = "username", unique = true),
        Index(name = "idx_users_role", columnList = "role")
    ]
)
open class UserEntity(
    @Id
    @Column(name = "user_id")
    open var userId: Int = 0,

    @Column(name = "name", nullable = false)
    open var name: String = "",

    @Column(name = "role", nullable = false)
    open var role: String = "",

    @Column(name = "username", nullable = false, unique = true)
    open var username: String = "",

    @Column(name = "password", nullable = false)
    open var password: String = "",

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null
)