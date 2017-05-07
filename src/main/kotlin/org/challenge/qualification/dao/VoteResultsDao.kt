package org.challenge.qualification.dao

import org.challenge.qualification.entity.VoteResultsEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 */
@Repository
interface VoteResultsDao : CrudRepository<VoteResultsEntity, UUID>