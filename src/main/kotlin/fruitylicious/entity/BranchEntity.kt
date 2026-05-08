package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "branches")
open class BranchEntity(
    @Id
    @Column(name = "branch_id")
    open var branchId: Int = 0,

    @Column(name = "branch_name", nullable = false)
    open var branchName: String = "",

    @Column(name = "address", nullable = false)
    open var address: String = "",

    @Column(name = "contact_number", nullable = false)
    open var contactNumber: String = "",

    @Lob
    @Column(name = "gcash_qr_image", columnDefinition = "CLOB")
    open var gcashQrImage: String? = null,

    @Column(name = "gcash_qr_image_type")
    open var gcashQrImageType: String? = null,

    @Column(name = "gcash_account_name")
    open var gcashAccountName: String? = null,

    @Column(name = "gcash_account_number")
    open var gcashAccountNumber: String? = null,

    @Column(name = "last_modified", nullable = false)
    open var lastModified: Long = 0L,

    @Column(name = "is_synced", nullable = false)
    open var isSynced: Boolean = true,

    @Column(name = "synced_at")
    open var syncedAt: Long? = null
)