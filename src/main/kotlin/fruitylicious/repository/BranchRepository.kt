package fruitylicious.repository

import fruitylicious.entity.BranchEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BranchRepository : JpaRepository<BranchEntity, Int> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<BranchEntity>
    fun countByLastModifiedGreaterThan(lastModified: Long): Long
}