package org.challenge.qualification.dto

import java.util.*

/**
 */
data class VoteDto(
        val delegateId: UUID?,
        val voteResultsId: UUID?,
        val result: VoteResultType
)
