package fruitylicious.repository.report

interface TransactionReportRow {
    val transactionId: String
    val userId: Int
    val userName: String
    val branchId: Int
    val totalAmount: Double
    val paymentType: String
    val dateTime: Long
    val status: String
}

interface TransactionLineRow {
    val transactionId: String
    val productId: Int
    val productName: String
    val quantity: Int
    val subtotal: Double
    val sizeName: String?
}