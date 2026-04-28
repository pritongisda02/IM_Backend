package fruitylicious.controller

import fruitylicious.dto.SyncStatusResponse
import fruitylicious.dto.SyncTriggerResponse
import fruitylicious.sync.SyncService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sync")
class SyncController(
    private val syncService: SyncService
) {

    // -------------------------------------------------------------------------
    // GET /api/sync/status
    // Staff + Admin — returns current sync state, last sync time, unsynced counts
    // -------------------------------------------------------------------------

    @GetMapping("/status")
    fun getStatus(): ResponseEntity<SyncStatusResponse> =
        ResponseEntity.ok(syncService.getStatus())

    // -------------------------------------------------------------------------
    // POST /api/sync/trigger
    // Admin only — manually kicks off an immediate sync cycle
    // -------------------------------------------------------------------------

    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    fun triggerSync(): ResponseEntity<SyncTriggerResponse> =
        ResponseEntity.ok(syncService.triggerManualSync())
}