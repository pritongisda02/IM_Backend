package fruitylicious.repository.oracle

import fruitylicious.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OracleUserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): User?

    fun findAllByLastModifiedAfter(lastModified: LocalDateTime): List<User>

    fun findAllByBranchId(branchId: Long): List<User>
}