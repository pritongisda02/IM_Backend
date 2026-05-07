package fruitylicious.repository

import fruitylicious.entity.InventoryAdjustmentEntity
import fruitylicious.repository.report.InventoryAdjustmentReportRow
import fruitylicious.repository.report.InventoryAdjustmentSummaryRow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InventoryAdjustmentRepository : JpaRepository<InventoryAdjustmentEntity, String> {

    fun findByLastModifiedGreaterThan(lastModified: Long): List<InventoryAdjustmentEntity>

    fun findByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): List<InventoryAdjustmentEntity>

    fun countByLastModifiedGreaterThan(lastModified: Long): Long

    fun countByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): Long

    @Query(
        """
        SELECT
            COALESCE(SUM(a.adjustmentAmount), 0) AS totalAdjustmentAmount,
            COUNT(a.adjustmentId) AS totalAdjustmentEntries
        FROM InventoryAdjustmentEntity a
        WHERE a.dateTime BETWEEN :from AND :to
        AND (:branchId IS NULL OR a.branchId = :branchId)
        """
    )
    fun getInventoryAdjustmentSummary(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): InventoryAdjustmentSummaryRow

    @Query(
        """
        SELECT
            a.adjustmentId AS adjustmentId,
            a.ingredientId AS ingredientId,
            i.ingredientName AS ingredientName,
            a.adjustmentAmount AS adjustmentAmount,
            i.unitType AS unitType,
            a.reason AS reason,
            a.userId AS userId,
            u.name AS userName,
            a.dateTime AS dateTime
        FROM InventoryAdjustmentEntity a
        JOIN IngredientEntity i ON a.ingredientId = i.ingredientId
        JOIN UserEntity u ON a.userId = u.userId
        WHERE a.branchId = :branchId
        AND a.dateTime BETWEEN :from AND :to
        ORDER BY a.dateTime DESC
        """
    )
    fun getInventoryAdjustmentReportRows(
        @Param("branchId") branchId: Int,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): List<InventoryAdjustmentReportRow>

    @Query(
        value = """
            SELECT
                a.adjustmentId AS adjustmentId,
                a.ingredientId AS ingredientId,
                i.ingredientName AS ingredientName,
                a.adjustmentAmount AS adjustmentAmount,
                i.unitType AS unitType,
                a.reason AS reason,
                a.userId AS userId,
                u.name AS userName,
                a.dateTime AS dateTime
            FROM InventoryAdjustmentEntity a
            JOIN IngredientEntity i ON a.ingredientId = i.ingredientId
            JOIN UserEntity u ON a.userId = u.userId
            WHERE a.dateTime BETWEEN :from AND :to
            AND (:branchId IS NULL OR a.branchId = :branchId)
            ORDER BY a.dateTime DESC
        """,
        countQuery = """
            SELECT COUNT(a)
            FROM InventoryAdjustmentEntity a
            WHERE a.dateTime BETWEEN :from AND :to
            AND (:branchId IS NULL OR a.branchId = :branchId)
        """
    )
    fun getInventoryAdjustmentReportRowsPage(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long,
        pageable: Pageable
    ): Page<InventoryAdjustmentReportRow>
}