package fruitylicious.service

import fruitylicious.config.AlreadyClockedInException
import fruitylicious.config.NotClockedInException
import fruitylicious.dto.ClockInResponse
import fruitylicious.dto.ClockOutResponse
import fruitylicious.dto.StaffLogResponse
import fruitylicious.entity.StaffLog
import fruitylicious.repository.local.LocalStaffLogRepository
import fruitylicious.repository.local.LocalUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class StaffService(
    private val localStaffLogRepository: LocalStaffLogRepository,
    private val localUserRepository: LocalUserRepository,
    private val auditService: AuditService
) {

    // -------------------------------------------------------------------------
    // Reads
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    fun getLogs(branchId: Long, isAdmin: Boolean, requestingBranchId: Long): List<StaffLogResponse> {
        return if (isAdmin) {
            localStaffLogRepository.findAll().map { it.toResponse() }
        } else {
            localStaffLogRepository.findAllByBranchId(requestingBranchId)
                .map { it.toResponse() }
        }
    }

    // -------------------------------------------------------------------------
    // Clock In
    // -------------------------------------------------------------------------

    @Transactional
    fun clockIn(userId: Long, branchId: Long): ClockInResponse {
        // Guard: block if already clocked in without clocking out
        if (localStaffLogRepository.hasActiveSession(userId)) {
            throw AlreadyClockedInException(
                "User $userId already has an active clock-in session. " +
                        "Please clock out before clocking in again."
            )
        }

        val staffLog = StaffLog().apply {
            this.userId   = userId
            this.branchId = branchId
            clockIn       = LocalDateTime.now()
            clockOut      = null
            lastModified  = LocalDateTime.now()
            isSynced      = false
        }
        val saved = localStaffLogRepository.save(staffLog)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.CLOCK_IN,
            tableAffected = "staff_logs",
            details       = "User $userId clocked in at ${saved.clockIn}"
        )

        return ClockInResponse(
            logId    = saved.logId,
            userId   = saved.userId,
            branchId = saved.branchId,
            clockIn  = saved.clockIn,
            message  = "Clock-in recorded successfully"
        )
    }

    // -------------------------------------------------------------------------
    // Clock Out
    // -------------------------------------------------------------------------

    @Transactional
    fun clockOut(userId: Long, branchId: Long): ClockOutResponse {
        val activeSessions = localStaffLogRepository.findActiveSession(userId)

        if (activeSessions.isEmpty()) {
            throw NotClockedInException(
                "User $userId has no active clock-in session to clock out from."
            )
        }

        // Take the most recent active session
        val activeSession = activeSessions.first()
        val clockOutTime  = LocalDateTime.now()

        activeSession.apply {
            clockOut     = clockOutTime
            lastModified = LocalDateTime.now()
            isSynced     = false
        }
        val saved = localStaffLogRepository.save(activeSession)

        auditService.log(
            userId        = userId,
            branchId      = branchId,
            action        = AuditAction.CLOCK_OUT,
            tableAffected = "staff_logs",
            details       = "User $userId clocked out at $clockOutTime " +
                    "(session id=${saved.logId})"
        )

        return ClockOutResponse(
            logId    = saved.logId,
            userId   = saved.userId,
            branchId = saved.branchId,
            clockIn  = saved.clockIn,
            clockOut = clockOutTime,
            message  = "Clock-out recorded successfully"
        )
    }

    // -------------------------------------------------------------------------
    // Mapper
    // -------------------------------------------------------------------------

    private fun StaffLog.toResponse(): StaffLogResponse {
        val user = localUserRepository.findById(userId).orElse(null)
        return StaffLogResponse(
            logId        = logId,
            userId       = userId,
            userName     = user?.name ?: "Unknown",
            branchId     = branchId,
            clockIn      = clockIn,
            clockOut     = clockOut,
            lastModified = lastModified,
            isSynced     = isSynced,
            syncedAt     = syncedAt
        )
    }
}