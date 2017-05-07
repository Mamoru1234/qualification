package org.challenge.qualification.daos

import org.challenge.qualification.entities.DataSetEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 */
//@Repository
interface DataSetDao : CrudRepository<DataSetEntity, UUID>