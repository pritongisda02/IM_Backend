package fruitylicious.repository

import fruitylicious.entity.StaffLogEntity
import fruitylicious.repository.report.StaffLogReportRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StaffLogRepository : JpaRepository<StaffLogEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<StaffLogEntity>

    @Query(
        """
        SELECT
            s.logId AS logId,
            s.userId AS userId,
            u.name AS userName,
            s.clockIn AS clockIn,
            s.clockOut AS clockOut,
            s.image AS image
        FROM StaffLogEntity s
        JOIN UserEntity u ON s.userId = u.userId
        WHERE s.branchId = :branchId
        AND s.clockIn BETWEEN :from AND :to
        ORDER BY s.clockIn DESC
        """
    )
    fun getStaffLogReportRows(
        @Param("branchId") branchId: Int,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): List<StaffLogReportRow>

    fun findByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): List<StaffLogEntity>
}