package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "staff_logs")
class StaffLog : BaseEntity() {

    @Id
    @Column(name = "log_id", nullable = false)
    var logId: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = User()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    var branch: Branch = Branch()

    @Column(name = "image", length = 500)
    var image: String? = null

    @Column(name = "clock_in", nullable = false)
    var clockIn: Instant = Instant.now()

    @Column(name = "clock_out")
    var clockOut: Instant? = null
}