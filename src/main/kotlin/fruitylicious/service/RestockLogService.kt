package fruitylicious.service

import fruitylicious.entity.RestockLog
import fruitylicious.repository.oracle.OracleRestockLogRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RestockLogService (
    private val restockLogRepository: OracleRestockLogRepository
)
{
    fun createRestockLog(log: RestockLog): RestockLog{
        return restockLogRepository.save(log)
    }

    fun getAllRestockLog(): List<RestockLog>{
        return restockLogRepository.findAll()
    }

    fun searchByDate(start: LocalDateTime, end: LocalDateTime): List<RestockLog>{
        return restockLogRepository.findByDateTimeBetween(start, end.plusDays(1))
    }
}