package fruitylicious.sync

data class HasUpdatesResponse(
    val hasUpdates: Boolean,
    val changedCount: Long,
    val serverTime: Long
)