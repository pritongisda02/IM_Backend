package fruitylicious.repository.oracle

import fruitylicious.entity.StaffLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OracleStaffLogRepository : JpaRepository<StaffLog, Long> {

    fun findAllByBranchId(branchId: Long): List<StaffLog>

    fun findAllByUserId(userId: Long): List<StaffLog>

    fun findAllByLastModifiedAfter(lastModified: LocalDateTime): List<StaffLog>

    fun findAllByBranchIdAndClockInBetween(
        branchId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<StaffLog>

    @Query(
        """
        SELECT s FROM StaffLog s
        WHERE s.userId = :userId
          AND s.clockOut IS NULL
        ORDER BY s.clockIn DESC
        """
    )
    fun findActiveSession(
        @Param("userId") userId: Long
    ): List<StaffLog>
}