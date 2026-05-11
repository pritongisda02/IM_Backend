package fruitylicious.repository

import fruitylicious.entity.RestockLogEntity
import fruitylicious.repository.report.RestockReportRow
import fruitylicious.repository.report.RestockSummaryRow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RestockLogRepository : JpaRepository<RestockLogEntity, String> {

    fun findByLastModifiedGreaterThan(lastModified: Long): List<RestockLogEntity>

    fun findByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): List<RestockLogEntity>

    fun countByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): Long

    @Query(
        """
        SELECT
            COALESCE(SUM(r.quantityAdded), 0) AS totalRestockQuantity,
            COUNT(r.restockId) AS totalRestockEntries
        FROM RestockLogEntity r
        WHERE r.dateTime BETWEEN :from AND :to
        AND (:branchId IS NULL OR r.branchId = :branchId)
        """
    )
    fun getRestockSummary(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): RestockSummaryRow

    @Query(
        """
        SELECT
            r.restockId AS restockId,
            r.ingredientId AS ingredientId,
            i.ingredientName AS ingredientName,
            r.quantityAdded AS quantityAdded,
            i.unitType AS unitType,
            r.supplier AS supplier,
            r.userId AS userId,
            u.name AS userName,
            r.dateTime AS dateTime
        FROM RestockLogEntity r
        JOIN IngredientEntity i ON r.ingredientId = i.ingredientId
        JOIN UserEntity u ON r.userId = u.userId
        WHERE r.branchId = :branchId
        AND r.dateTime BETWEEN :from AND :to
        ORDER BY r.dateTime DESC
        """
    )
    fun getRestockReportRows(
        @Param("branchId") branchId: Int,
        @Param("from") from: Long,
        @Param("to") to: Long
    ): List<RestockReportRow>

    @Query(
        value = """
            SELECT
                r.restockId AS restockId,
                r.ingredientId AS ingredientId,
                i.ingredientName AS ingredientName,
                r.quantityAdded AS quantityAdded,
                i.unitType AS unitType,
                r.supplier AS supplier,
                r.userId AS userId,
                u.name AS userName,
                r.dateTime AS dateTime
            FROM RestockLogEntity r
            JOIN IngredientEntity i ON r.ingredientId = i.ingredientId
            JOIN UserEntity u ON r.userId = u.userId
            WHERE r.dateTime BETWEEN :from AND :to
            AND (:branchId IS NULL OR r.branchId = :branchId)
            ORDER BY r.dateTime DESC
        """,
        countQuery = """
            SELECT COUNT(r)
            FROM RestockLogEntity r
            WHERE r.dateTime BETWEEN :from AND :to
            AND (:branchId IS NULL OR r.branchId = :branchId)
        """
    )
    fun getRestockReportRowsPage(
        @Param("branchId") branchId: Int?,
        @Param("from") from: Long,
        @Param("to") to: Long,
        pageable: Pageable
    ): Page<RestockReportRow>
}