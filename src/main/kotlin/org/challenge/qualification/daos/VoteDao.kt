package org.challenge.qualification.daos

import org.challenge.qualification.entities.VoteEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 */
@Repository
interface VoteDao : CrudRepository<VoteEntity, UUID>