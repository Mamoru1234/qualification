package org.challenge.qualification.entity

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "test")
class TestEntity (
        var firstName: String = "",
        @Id
        @GeneratedValue
        @Type(type = "pg-uuid")
        var id: UUID = UUID.randomUUID()
)