package org.challenge.qualification.dtos

import java.util.*

/**
 */
data class VoteResultsDto(
        val id: UUID,
        val topic: String,
        val metaData: String,
        val voteSessionId: UUID?
)