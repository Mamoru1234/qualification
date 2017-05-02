package org.challenge.qualification.dto

enum class VoteResultType {
    POSITIVE, NEGATIVE, ABSTAINED, IGNORED, ABSENT
}

data class VoteResults(
        val topic: String,
        val votes: Map<String, VoteResultType>
)