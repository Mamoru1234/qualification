package org.challenge.qualification.votes.scanner

enum class VoteResultType {
    POSITIVE, NEGATIVE, ABSTAINED, IGNORED, ABSENT
}

data class VoteResults(
        val topic: String,
        val voteMetaData: String,
        val votes: Map<Delegate, VoteResultType>
)