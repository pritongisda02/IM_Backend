package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {

    @Column(name = "last_modified", nullable = false)
    var lastModified: LocalDateTime = LocalDateTime.now()

    @Column(name = "is_synced", nullable = false)
    var isSynced: Boolean = false

    @Column(name = "synced_at")
    var syncedAt: LocalDateTime? = null
}