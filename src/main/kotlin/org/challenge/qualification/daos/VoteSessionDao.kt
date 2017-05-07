package org.challenge.qualification.daos

import org.challenge.qualification.entities.VoteSessionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 */
@Repository
interface VoteSessionDao :CrudRepository<VoteSessionEntity, UUID>