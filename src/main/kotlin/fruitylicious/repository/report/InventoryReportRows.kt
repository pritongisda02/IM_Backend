package fruitylicious.repository.report

interface InventoryReportRow {
    val ingredientId: Int
    val ingredientName: String
    val unitType: String
    val currentStock: Double
    val lowStockThreshold: Double
}
