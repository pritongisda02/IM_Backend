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