package fruitylicious.repository.report

interface TransactionReportRow {
    val transactionId: String
    val transactionName: String?
    val userId: Int
    val userName: String
    val branchId: Int
    val totalAmount: Double
    val paymentType: String
    val dateTime: Long
    val status: String
}

interface TransactionLineRow {
    val transactionItemId: String
    val transactionId: String
    val productId: Int
    val productName: String
    val quantity: Int
    val subtotal: Double
    val sizeName: String?
}

interface TransactionAddonLineRow {
    val transactionItemId: String
    val addonProductId: Int
    val addonName: String
    val quantity: Int
    val subtotal: Double
}