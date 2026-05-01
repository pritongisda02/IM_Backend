package fruitylicious.repository.report

interface StaffLogReportRow {
    val logId: String
    val userId: Int
    val userName: String
    val clockIn: Long
    val clockOut: Long?
    val image: String?
}

interface AuditLogReportRow {
    val logId: String
    val userId: Int
    val userName: String
    val action: String
    val tableAffected: String
    val timestamp: Long
}