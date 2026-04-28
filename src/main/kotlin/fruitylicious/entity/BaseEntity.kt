package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.time.Instant

@MappedSuperclass
abstract class BaseEntity {

    @Column(name = "last_modified", nullable = false)
    var lastModified: Instant = Instant.now()
}