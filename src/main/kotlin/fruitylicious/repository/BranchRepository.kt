package fruitylicious.repository

import fruitylicious.entity.Branch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface BranchRepository : JpaRepository<Branch, Long> {

    fun findAllByLastModifiedAfter(since: Instant): List<Branch>
}