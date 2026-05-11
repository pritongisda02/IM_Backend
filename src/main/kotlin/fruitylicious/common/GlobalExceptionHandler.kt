package fruitylicious.common

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ApiErrorResponse(
    val success: Boolean = false,
    val message: String,
    val details: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        exception: IllegalArgumentException
    ): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(
                ApiErrorResponse(
                    message = exception.message ?: "Invalid request."
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        exception: MethodArgumentNotValidException
    ): ResponseEntity<ApiErrorResponse> {
        val errors = exception.bindingResult.fieldErrors.map {
            "${it.field}: ${it.defaultMessage}"
        }

        return ResponseEntity
            .badRequest()
            .body(
                ApiErrorResponse(
                    message = "Validation failed.",
                    details = errors
                )
            )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(
        exception: DataIntegrityViolationException
    ): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ApiErrorResponse(
                    message = "Database constraint violation.",
                    details = listOf(exception.rootCause?.message ?: exception.message ?: "")
                )
            )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(
        exception: AccessDeniedException
    ): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                ApiErrorResponse(
                    message = "Access denied."
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        exception: Exception
    ): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiErrorResponse(
                    message = "Internal server error.",
                    details = listOf(exception.message ?: "Unknown error.")
                )
            )
    }
}