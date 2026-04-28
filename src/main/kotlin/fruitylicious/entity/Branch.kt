package fruitylicious.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "branches")
class Branch : BaseEntity() {

    @Id
    @Column(name = "branch_id", nullable = false)
    var branchId: Long = 0

    @Column(name = "branch_name", nullable = false, length = 150)
    var branchName: String = ""

    @Column(name = "address", nullable = false, length = 500)
    var address: String = ""

    @Column(name = "contact_number", nullable = false, length = 20)
    var contactNumber: String = ""
}