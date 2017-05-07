package org.challenge.qualification.entity

import org.challenge.qualification.dto.DataSetDto
import org.hibernate.annotations.Type
import java.time.LocalDate
import java.util.*
import javax.persistence.*

/**
 */
@Entity
@Table(name = "data_set")
data class DataSetEntity (
        @Id
        @Type(type = "pg-uuid")
        var id: UUID = UUID.randomUUID(),

        var created: LocalDate = LocalDate.now(),

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "dataSet")
        var sessions: List<VoteSessionEntity>? = null
)

fun dataSetEntityToDto(dataSetEntity: DataSetEntity): DataSetDto {
        return DataSetDto(
                id = dataSetEntity.id,
                created = dataSetEntity.created
        )
}
