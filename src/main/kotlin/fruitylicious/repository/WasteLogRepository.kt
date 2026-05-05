package fruitylicious.repository

import fruitylicious.entity.WasteLogEntity
import fruitylicious.repository.report.WasteReportRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface WasteLogRepository : JpaRepository<WasteLogEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<WasteLogEntity>

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

    fun findByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): List<WasteLogEntity>

    fun countByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): Long
}