package org.challenge.qualification.entity

import org.challenge.qualification.dto.VoteSessionDto
import org.hibernate.annotations.Type
import java.time.LocalDate
import java.util.*
import javax.persistence.*

/**
 */
@Entity
@Table(name = "vote_session")
data class VoteSessionEntity (
        @Id
        @Type(type = "pg-uuid")
        var id: UUID = UUID.randomUUID(),

        @Column(nullable = false)
        var sessionNumber: Int = 0,
        @Column(nullable = false)
        var sessionDate: LocalDate = LocalDate.now(),

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "fk_data_set_id")
        var dataSet: DataSetEntity? = null,

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "voteSession")
        var voteResults: List<VoteResultsEntity>? = null
        )

fun voteSessionEntityToDto(voteSessionEntity: VoteSessionEntity): VoteSessionDto {
        return VoteSessionDto(
                id = voteSessionEntity.id,
                sessionNumber = voteSessionEntity.sessionNumber,
                sessionDate = voteSessionEntity.sessionDate,
                dataSetId = voteSessionEntity.dataSet?.id
        )
}
