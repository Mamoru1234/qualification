package org.challenge.qualification.dao

import org.challenge.qualification.entity.TestEntity
import org.springframework.data.repository.CrudRepository

interface TestRepo: CrudRepository<TestEntity, Long>