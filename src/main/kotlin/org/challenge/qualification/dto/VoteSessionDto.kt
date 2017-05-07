package org.challenge.qualification.dto

import java.time.LocalDate
import java.util.*

/**
 */
data class VoteSessionDto(
        val id: UUID,
        val sessionNumber: Int,
        val sessionDate: LocalDate,
        val dataSetId: UUID?
)