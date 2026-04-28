package fruitylicious.repository.local

import fruitylicious.entity.Branch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocalBranchRepository : JpaRepository<Branch, Long> {

    fun findByBranchName(branchName: String): Branch?

    fun findAllByIsSyncedFalse(): List<Branch>
}