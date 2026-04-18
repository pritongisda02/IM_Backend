package fruitylicious.entity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "AUDIT_LOGS")
class AuditLog(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_seq")
    @SequenceGenerator(name = "audit_seq", sequenceName = "AUDIT_SEQ", allocationSize = 1)
    @Column(name = "LOG_ID")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    val user: User,

    val action: String,

    val tableAffected: String,

    val timestamp: LocalDateTime
)