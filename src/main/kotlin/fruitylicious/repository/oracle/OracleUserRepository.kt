package fruitylicious.repository.oracle

import fruitylicious.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface OracleUserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}