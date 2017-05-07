package org.challenge.qualification.daos

import org.challenge.qualification.entities.VoteResultsEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 */
@Repository
interface VoteResultsDao : PagingAndSortingRepository<VoteResultsEntity, UUID>