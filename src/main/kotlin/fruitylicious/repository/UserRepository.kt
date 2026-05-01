package fruitylicious.repository

import fruitylicious.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Int> {
    fun findByUsername(username: String): UserEntity?
    fun findByLastModifiedGreaterThan(lastModified: Long): List<UserEntity>
}