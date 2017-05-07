package org.challenge.qualification.entity

import org.challenge.qualification.dto.VoteDto
import org.challenge.qualification.dto.VoteResultType
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

/**
 */
@Entity
@Table(name = "vote")
data class VoteEntity (
        @Id
        @Type(type = "pg-uuid")
        var id: UUID = UUID.randomUUID(),

        var result: VoteResultType = VoteResultType.ABSTAINED,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "fk_delegate_id")
        var delegate: DelegateEntity? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "fk_voting_results_id")
        var voteResults: VoteResultsEntity? = null
        )

fun voteEntityToDto(voteEntity: VoteEntity): VoteDto {
        return VoteDto(
                delegateId = voteEntity.delegate?.id,
                voteResultsId = voteEntity.voteResults?.id,
                result = voteEntity.result
        )
}
