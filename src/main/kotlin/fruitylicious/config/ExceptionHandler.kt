package fruitylicious.config

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

// ---------------------------------------------------------------------------
// Unified error response body
// ---------------------------------------------------------------------------

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)

data class ValidationErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int = HttpStatus.BAD_REQUEST.value(),
    val error: String = "Validation Failed",
    val errors: Map<String, String>,
    val path: String
)

// ---------------------------------------------------------------------------
// Custom exception types
// ---------------------------------------------------------------------------

class InsufficientStockException(message: String) : RuntimeException(message)
class AlreadyClockedInException(message: String) : RuntimeException(message)
class NotClockedInException(message: String) : RuntimeException(message)
class DuplicateResourceException(message: String) : RuntimeException(message)
class SyncException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

// ---------------------------------------------------------------------------
// Global exception handler
// ---------------------------------------------------------------------------

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(
        ex: EntityNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> =
        buildError(HttpStatus.NOT_FOUND, ex.message ?: "Resource not found", request)

    @ExceptionHandler(InsufficientStockException::class)
    fun handleInsufficientStock(
        ex: InsufficientStockException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> =
        buildError(HttpStatus.UNPROCESSABLE_ENTITY, ex.message ?: "Insufficient stock", request)

    @ExceptionHandler(AlreadyClockedInException::class)
    fun handleAlreadyClockedIn(
        ex: AlreadyClockedInException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> =
        buildError(HttpStatus.CONFLICT, ex.message ?: "Already clocked in", request)

    @ExceptionHandler(NotClockedInException::class)
    fun handleNotClockedIn(
        ex: NotClockedInException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> =
        buildError(HttpStatus.CONFLICT, ex.message ?: "Not clocked in", request)

    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicate(
        ex: DuplicateResourceException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> =
        buildError(HttpStatus.CONFLICT, ex.message ?: "Duplicate resource", request)

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(
        ex: BadCredentialsException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> =
        buildError(HttpStatus.UNAUTHORIZED, "Invalid username or password", request)

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(
        ex: AccessDeniedException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> =
        buildError(HttpStatus.FORBIDDEN, "Access denied", request)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ValidationErrorResponse> {
        val fieldErrors = ex.bindingResult.allErrors.associate { error ->
            val field = if (error is FieldError) error.field else error.objectName
            field to (error.defaultMessage ?: "Invalid value")
        }
        return ResponseEntity(
            ValidationErrorResponse(
                errors = fieldErrors,
                path = request.getDescription(false).removePrefix("uri=")
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> =
        buildError(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request", request)

    @ExceptionHandler(SyncException::class)
    fun handleSync(
        ex: SyncException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> =
        buildError(HttpStatus.SERVICE_UNAVAILABLE, ex.message ?: "Sync error", request)

    @ExceptionHandler(Exception::class)
    fun handleGeneral(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> =
        buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request)

    // -----------------------------------------------------------------------

    private fun buildError(
        status: HttpStatus,
        message: String,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(body, status)
    }
}