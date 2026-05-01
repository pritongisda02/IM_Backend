package fruitylicious.sync

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sync/conflicts")
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
class SyncConflictController(
    private val syncConflictService: SyncConflictService
) {

    @GetMapping
    fun getConflicts(
        @RequestParam(required = false) branchId: Int?
    ): ResponseEntity<List<SyncConflictDto>> {
        return ResponseEntity.ok(
            syncConflictService.getConflicts(branchId)
        )
    }
}