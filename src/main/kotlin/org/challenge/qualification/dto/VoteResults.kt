package org.challenge.qualification.dto

enum class VoteResultType {
    POSITIVE, NEGATIVE, ABSTAINED, IGNORED, ABSENT
}

data class VoteResults(
        val topic: String,
        val voteMetaData: String,
        val votes: Map<Delegate, VoteResultType>
)