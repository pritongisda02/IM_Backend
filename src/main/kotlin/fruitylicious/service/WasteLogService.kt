package fruitylicious.service

import fruitylicious.entity.WasteLog
import fruitylicious.repository.WasteLogRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class WasteLogService (
    private val wasteLogRepository: WasteLogRepository
)
{
    fun createWasteLog(waste: WasteLog): WasteLog{
        return wasteLogRepository.save(waste)
    }

    fun getAllWasteLog(): List<WasteLog>{
        return wasteLogRepository.findAll()
    }

    fun searchByDate(start: LocalDateTime, end: LocalDateTime): List<WasteLog>{
        return wasteLogRepository.findByDateTimeBetween(start, end.plusDays(1))
    }
}