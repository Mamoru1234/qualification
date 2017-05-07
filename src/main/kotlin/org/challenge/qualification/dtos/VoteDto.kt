package org.challenge.qualification.dtos

import org.challenge.qualification.votes.scanner.VoteResultType
import java.util.*

/**
 */
data class VoteDto(
        val delegateId: UUID?,
        val voteResultsId: UUID?,
        val result: VoteResultType
)
