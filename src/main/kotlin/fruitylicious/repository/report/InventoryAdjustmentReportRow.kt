package fruitylicious.repository.report

interface InventoryAdjustmentReportRow {
    val adjustmentId: String
    val ingredientId: Int
    val ingredientName: String
    val adjustmentAmount: Double
    val unitType: String
    val reason: String
    val userId: Int
    val userName: String
    val dateTime: Long
}

interface InventoryAdjustmentSummaryRow {
    val totalAdjustmentAmount: Double?
    val totalAdjustmentEntries: Long?
}