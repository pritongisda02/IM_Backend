package fruitylicious.dto

import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class BranchRequest(
    val branchId: Long = 0,

    @field:NotBlank(message = "Branch name is required")
    val branchName: String,

    @field:NotBlank(message = "Address is required")
    val address: String,

    @field:NotBlank(message = "Contact number is required")
    val contactNumber: String
)

data class BranchResponse(
    val branchId: Long,
    val branchName: String,
    val address: String,
    val contactNumber: String,
    val lastModified: Instant
)