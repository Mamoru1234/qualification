package org.challenge.qualification.dtos

import java.util.*

/**
 */
data class DelegateDto(
        val id: UUID? = null,
        val firstName: String,
        val middleName: String,
        val lastName: String
)