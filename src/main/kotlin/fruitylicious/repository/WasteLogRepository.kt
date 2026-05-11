package fruitylicious.repository

import fruitylicious.entity.WasteLogEntity
import fruitylicious.repository.report.WasteReportRow
import fruitylicious.repository.report.WasteSummaryRow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface WasteLogRepository : JpaRepository<WasteLogEntity, String> {

    fun findByLastModifiedGreaterThan(lastModified: Long): List<WasteLogEntity>

    fun findByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): List<WasteLogEntity>

    fun countByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): Long

    @Query(
        """
        SELECT
            COALESCE(SUM(w.quantity), 0) AS totalWasteQuantity,
            COUNT(w.wasteId) AS totalWasteEntries
        FROM WasteLogEntity w
        WHERE w.dateTime BETWEEN :from AND :to
        AND (:branchId IS NULL OR w.branchId = :branchId)
        """
    )
    fun getWasteSummary(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): WasteSummaryRow

    @Query(
        """
        SELECT
            w.wasteId AS wasteId,
            w.ingredientId AS ingredientId,
            i.ingredientName AS ingredientName,
            w.quantity AS quantity,
            i.unitType AS unitType,
            w.reason AS reason,
            w.userId AS userId,
            u.name AS userName,
            w.dateTime AS dateTime,
            w.image AS image
        FROM WasteLogEntity w
        JOIN IngredientEntity i ON w.ingredientId = i.ingredientId
        JOIN UserEntity u ON w.userId = u.userId
        WHERE w.branchId = :branchId
        AND w.dateTime BETWEEN :from AND :to
        ORDER BY w.dateTime DESC
        """
    )
    fun getWasteReportRows(
        @Param("branchId") branchId: Int,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): List<WasteReportRow>

    @Query(
        value = """
            SELECT
                w.wasteId AS wasteId,
                w.ingredientId AS ingredientId,
                i.ingredientName AS ingredientName,
                w.quantity AS quantity,
                i.unitType AS unitType,
                w.reason AS reason,
                w.userId AS userId,
                u.name AS userName,
                w.dateTime AS dateTime,
                w.image AS image
            FROM WasteLogEntity w
            JOIN IngredientEntity i ON w.ingredientId = i.ingredientId
            JOIN UserEntity u ON w.userId = u.userId
            WHERE w.dateTime BETWEEN :from AND :to
            AND (:branchId IS NULL OR w.branchId = :branchId)
            ORDER BY w.dateTime DESC
        """,
        countQuery = """
            SELECT COUNT(w)
            FROM WasteLogEntity w
            WHERE w.dateTime BETWEEN :from AND :to
            AND (:branchId IS NULL OR w.branchId = :branchId)
        """
    )
    fun getWasteReportRowsPage(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long,
        pageable: Pageable
    ): Page<WasteReportRow>
}