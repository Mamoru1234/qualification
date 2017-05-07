package org.challenge.qualification.entity

import org.challenge.qualification.dto.VoteResultsDto
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

/**
 */
@Entity
@Table(name = "vote_results")
data class
VoteResultsEntity(
        @Id
        @Type(type = "pg-uuid")
        var id: UUID = UUID.randomUUID(),

        var topic:String = "",

        var metaData: String = "",

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "fk_vote_session_id")
        var voteSession: VoteSessionEntity? = null,

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "voteResults")
        var votes: List<VoteEntity>? = null
        )

fun voteResultsEntityToDto(voteResultsEntity: VoteResultsEntity): VoteResultsDto {
        return VoteResultsDto(
                id = voteResultsEntity.id,
                topic = voteResultsEntity.topic,
                metaData = voteResultsEntity.metaData,
                voteSessionId = voteResultsEntity.voteSession?.id
        )
}
