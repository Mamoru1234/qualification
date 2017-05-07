package org.challenge.qualification.dao

import org.challenge.qualification.entity.DelegateEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 */
@Repository
interface DelegateDao : CrudRepository<DelegateEntity, UUID> {
    fun findTopByFirstNameAndMiddleNameAndLastNameAllIgnoreCase(
            firstName: String,
            middleName: String,
            lastName: String
    ): DelegateEntity?
}