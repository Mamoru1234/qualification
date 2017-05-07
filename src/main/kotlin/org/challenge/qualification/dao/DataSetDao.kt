package org.challenge.qualification.dao

import org.challenge.qualification.entity.DataSetEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 */
//@Repository
interface DataSetDao : CrudRepository<DataSetEntity, UUID>