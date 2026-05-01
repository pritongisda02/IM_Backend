package fruitylicious.repository

import fruitylicious.entity.RestockLogEntity
import fruitylicious.repository.report.RestockReportRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RestockLogRepository : JpaRepository<RestockLogEntity, String> {
    fun findByLastModifiedGreaterThan(lastModified: Long): List<RestockLogEntity>

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

    fun findByBranchIdAndLastModifiedGreaterThan(
        branchId: Int,
        lastModified: Long
    ): List<RestockLogEntity>
}