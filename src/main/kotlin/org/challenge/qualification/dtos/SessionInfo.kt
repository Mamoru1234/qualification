package org.challenge.qualification.dtos

import java.time.LocalDate

/**
 */
data class SessionInfo(
        val sessionNumber: Int,
        val sessionDate: LocalDate
)