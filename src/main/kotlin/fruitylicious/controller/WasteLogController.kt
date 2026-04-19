package fruitylicious.controller

import fruitylicious.entity.WasteLog
import fruitylicious.service.WasteLogService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/waste")
class WasteLogController (
    private val wasteLogService: WasteLogService
)
{
    @PostMapping
    fun createWasteLog(waste: WasteLog): WasteLog{
        return wasteLogService.createWasteLog(waste)
    }

    @GetMapping
    fun getAllWasteLog(): List<WasteLog>{
        return wasteLogService.getAllWasteLog()
    }

    @GetMapping("/search")
    fun searchByDate(start: LocalDateTime, end: LocalDateTime): List<WasteLog>{
        return wasteLogService.searchByDate(start, end)
    }
}