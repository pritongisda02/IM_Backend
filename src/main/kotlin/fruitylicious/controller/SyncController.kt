package fruitylicious.controller

import fruitylicious.dto.SyncPullResponse
import fruitylicious.dto.SyncPushRequest
import fruitylicious.dto.SyncPushResponse
import fruitylicious.service.SyncService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/sync")
class SyncController(
    private val syncService: SyncService
) {

    @PostMapping("/push")
    fun push(
        @RequestBody request: SyncPushRequest
    ): ResponseEntity<SyncPushResponse> {
        val response = syncService.push(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/pull")
    fun pull(
        @RequestParam("since")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        since: Instant
    ): ResponseEntity<SyncPullResponse> {
        val response = syncService.pull(since)
        return ResponseEntity.ok(response)
    }
}