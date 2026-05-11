package fruitylicious.repository.report

interface SalesSummaryRow {
    val totalSales: Double?
    val totalTransactions: Long?
}

interface SalesItemRow {
    val productId: Int
    val productName: String
    val quantitySold: Long
    val grossSales: Double
}

interface PaymentBreakdownRow {
    val paymentType: String
    val transactionCount: Long
    val totalAmount: Double
}