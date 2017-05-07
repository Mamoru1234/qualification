package org.challenge.qualification.dao

import org.challenge.qualification.entity.VoteSessionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 */
@Repository
interface VoteSessionDao :CrudRepository<VoteSessionEntity, UUID>