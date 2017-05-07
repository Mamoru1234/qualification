package org.challenge.qualification.votes.analyser

import org.challenge.qualification.entities.DelegateEntity
import org.challenge.qualification.entities.VoteEntity
import org.challenge.qualification.entities.VoteResultsEntity
import org.challenge.qualification.votes.scanner.VoteResultType

class VoteSessionAnalyserResult(
        val count: Int = 0,
        val topics: List<String> = emptyList()
) {
    operator fun plus( result: VoteSessionAnalyserResult): VoteSessionAnalyserResult {
        return VoteSessionAnalyserResult(
                count = this.count + result.count,
                topics = this.topics + result.topics
        )
    }
}

private fun splitOnTypeGroups(
        votes: List<VoteEntity>,
        acceptedVoteTypes: List<VoteResultType>
): Map<VoteResultType, Set<DelegateEntity>> {
    var result = emptyMap<VoteResultType, Set<DelegateEntity>>()
    votes.forEach {
        if (it.delegate != null && acceptedVoteTypes.contains(it.result)) {
            val delegates = result.getOrDefault(it.result, emptySet()) + it.delegate!!
            result += it.result to delegates
        }
    }
    return result
}

/**
 */
fun analyseVoteSession(
        voteResults: List<VoteResultsEntity>?,
        voteResultTypes: List<VoteResultType> = VoteResultType.values().toList(),
        groupThreshold: Int = 1
): Map<Set<DelegateEntity>, VoteSessionAnalyserResult> {
    var result = emptyMap<Set<DelegateEntity>, VoteSessionAnalyserResult>()
    voteResults?.forEach {
        val groups = splitOnTypeGroups(it.votes!!, voteResultTypes).filterValues { it.size > groupThreshold }
        val increment = VoteSessionAnalyserResult(
                count = 1,
                topics = listOf(it.topic)
        )
        groups.forEach { type, delegates ->
            result.iterator().forEach {
                val intersection = it.key.intersect(delegates)
                if (intersection.size > groupThreshold && intersection != delegates) {
                    var count = result.getOrDefault(intersection, VoteSessionAnalyserResult())
                    if (it.key != intersection) {
                          count += it.value
                    }
                    result += intersection to count + increment
                }
            }
            val count = result.getOrDefault(delegates, VoteSessionAnalyserResult()) + increment
            result += delegates to count
        }
    }
    return result
}
