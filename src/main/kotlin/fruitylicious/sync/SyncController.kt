package fruitylicious.sync

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sync")
class SyncController(
    private val syncService: SyncService
) {

    @PostMapping("/push")
    fun push(
        @RequestHeader("X-BRANCH-ID") branchId: Int,
        @RequestBody request: PushRequest
    ): ResponseEntity<PushResponse> {
        return ResponseEntity.ok(
            syncService.push(
                request = request,
                requestingBranchId = branchId
            )
        )
    }

    @GetMapping("/pull")
    fun pull(
        @RequestHeader("X-BRANCH-ID") branchId: Int,
        @RequestParam since: Long
    ): ResponseEntity<PullResponse> {
        return ResponseEntity.ok(
            syncService.pull(
                requestingBranchId = branchId,
                since = since
            )
        )
    }
}