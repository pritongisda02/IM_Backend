package fruitylicious.repository.local

import fruitylicious.entity.StaffLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LocalStaffLogRepository : JpaRepository<StaffLog, Long> {

    fun findAllByBranchId(branchId: Long): List<StaffLog>

    fun findAllByUserId(userId: Long): List<StaffLog>

    fun findAllByUserIdAndBranchId(userId: Long, branchId: Long): List<StaffLog>

    fun findAllByBranchIdAndClockInBetween(
        branchId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<StaffLog>

    fun findAllByIsSyncedFalse(): List<StaffLog>

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

    @Query(
        """
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
        FROM StaffLog s
        WHERE s.userId = :userId
          AND s.clockOut IS NULL
        """
    )
    fun hasActiveSession(
        @Param("userId") userId: Long
    ): Boolean
}