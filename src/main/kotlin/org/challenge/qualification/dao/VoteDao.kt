package org.challenge.qualification.dao

import org.challenge.qualification.entity.VoteEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 */
@Repository
interface VoteDao : CrudRepository<VoteEntity, UUID>