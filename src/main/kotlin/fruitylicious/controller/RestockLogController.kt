package fruitylicious.controller

import fruitylicious.entity.RestockLog
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/RestockLog")
class RestockLogController (
    private val restockLogController: RestockLogController
)
{
    fun createRestockLog(@RequestBody log: RestockLog): RestockLog{
        return restockLogController.createRestockLog(log)
    }

    fun getAllRestockLog(): List<RestockLog>{
        return restockLogController.getAllRestockLog()
    }

    fun searchByDate(start: LocalDateTime, end: LocalDateTime): List<RestockLog>{
        return restockLogController.searchByDate(start,end)
    }
}