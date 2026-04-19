package fruitylicious.controller

import fruitylicious.entity.RestockLog
import fruitylicious.service.RestockLogService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/restockLog")
class RestockLogController (
    private val restockLogService: RestockLogService
)
{
    @PostMapping
    fun createRestockLog(@RequestBody log: RestockLog): RestockLog{
        return restockLogService.createRestockLog(log)
    }

    @GetMapping
    fun getAllRestockLog(): List<RestockLog>{
        return restockLogService.getAllRestockLog()
    }

    @GetMapping("/searchByDate")
    fun searchByDate(start: LocalDateTime, end: LocalDateTime): List<RestockLog>{
        return restockLogService.searchByDate(start,end)
    }
}