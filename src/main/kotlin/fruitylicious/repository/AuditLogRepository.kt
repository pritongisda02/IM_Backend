package fruitylicious.repository

import fruitylicious.entity.AuditLogEntity
import fruitylicious.repository.report.AuditLogReportRow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AuditLogRepository : JpaRepository<AuditLogEntity, String> {

    fun findByLastModifiedGreaterThan(lastModified: Long): List<AuditLogEntity>

    fun findByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): List<AuditLogEntity>

    fun countByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): Long

    @Query(
        """
        SELECT
            a.logId AS logId,
            a.userId AS userId,
            u.name AS userName,
            a.action AS action,
            a.tableAffected AS tableAffected,
            a.timestamp AS timestamp
        FROM AuditLogEntity a
        JOIN UserEntity u ON a.userId = u.userId
        WHERE a.branchId = :branchId
        AND a.timestamp BETWEEN :from AND :to
        ORDER BY a.timestamp DESC
        """
    )
    fun getAuditLogReportRows(
        @Param("branchId") branchId: Int,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): List<AuditLogReportRow>

    @Query(
        value = """
            SELECT
                a.logId AS logId,
                a.userId AS userId,
                u.name AS userName,
                a.action AS action,
                a.tableAffected AS tableAffected,
                a.timestamp AS timestamp
            FROM AuditLogEntity a
            JOIN UserEntity u ON a.userId = u.userId
            WHERE a.timestamp BETWEEN :from AND :to
            AND (:branchId IS NULL OR a.branchId = :branchId)
            ORDER BY a.timestamp DESC
        """,
        countQuery = """
            SELECT COUNT(a)
            FROM AuditLogEntity a
            WHERE a.timestamp BETWEEN :from AND :to
            AND (:branchId IS NULL OR a.branchId = :branchId)
        """
    )
    fun getAuditLogReportRowsPage(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long,
        pageable: Pageable
    ): Page<AuditLogReportRow>
}

