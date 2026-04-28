package fruitylicious.repository.local

import fruitylicious.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocalUserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): User?

    fun existsByUsername(username: String): Boolean

    fun findAllByRole(role: String): List<User>

    fun findAllByBranchId(branchId: Long): List<User>

    fun findAllByIsSyncedFalse(): List<User>
}