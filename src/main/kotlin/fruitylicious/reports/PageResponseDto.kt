package fruitylicious.reports

import org.springframework.data.domain.Page

data class PageResponseDto<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val totalItems: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        fun <T> fromPage(pageResult: Page<T>): PageResponseDto<T> {
            return PageResponseDto(
                items = pageResult.content,
                page = pageResult.number,
                size = pageResult.size,
                totalItems = pageResult.totalElements,
                totalPages = pageResult.totalPages,
                hasNext = pageResult.hasNext(),
                hasPrevious = pageResult.hasPrevious()
            )
        }
    }
}