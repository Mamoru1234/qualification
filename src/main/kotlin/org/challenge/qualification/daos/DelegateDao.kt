package org.challenge.qualification.daos

import org.challenge.qualification.entities.DelegateEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 */
@Repository
interface DelegateDao : PagingAndSortingRepository<DelegateEntity, UUID>