package fruitylicious.repository.report

interface WasteReportRow {
    val wasteId: String
    val ingredientId: Int
    val ingredientName: String
    val quantity: Double
    val unitType: String
    val reason: String
    val userId: Int
    val userName: String
    val dateTime: Long
    val image: String?
}

interface WasteSummaryRow {
    val totalWasteQuantity: Double?
    val totalWasteEntries: Long?
}

interface RestockReportRow {
    val restockId: String
    val ingredientId: Int
    val ingredientName: String
    val quantityAdded: Double
    val unitType: String
    val supplier: String
    val userId: Int
    val userName: String
    val dateTime: Long
}

interface RestockSummaryRow {
    val totalRestockQuantity: Double?
    val totalRestockEntries: Long?
}
