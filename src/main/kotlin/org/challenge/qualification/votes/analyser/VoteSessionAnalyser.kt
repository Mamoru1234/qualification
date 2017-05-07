package org.challenge.qualification.votes.analyser

import org.challenge.qualification.entities.DelegateEntity
import org.challenge.qualification.entities.VoteEntity
import org.challenge.qualification.entities.VoteResultsEntity
import org.challenge.qualification.votes.scanner.VoteResultType
import java.util.*

class VoteSessionAnalyserResult(
        val count: Int = 0,
        val voteResultIds: List<UUID> = emptyList()
) {
    operator fun plus( result: VoteSessionAnalyserResult): VoteSessionAnalyserResult {
        return VoteSessionAnalyserResult(
                count = this.count + result.count,
                voteResultIds = this.voteResultIds + result.voteResultIds
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

val GROUP_MIN_SIZE = 2
/**
 */
fun analyseVoteSession(
        voteResults: List<VoteResultsEntity>?,
        voteResultTypes: List<VoteResultType> = VoteResultType.values().toList(),
        groupThreshold: Int = 90
): Map<Set<DelegateEntity>, VoteSessionAnalyserResult> {
    var result = emptyMap<Set<DelegateEntity>, VoteSessionAnalyserResult>()
    val threshold = voteResults?.size?.let { (groupThreshold/100f)*it } ?:0f
    println("threshold:$threshold")
    voteResults?.forEachIndexed { index, voteResultsEntity ->
        val recordsLeft = voteResults.size - index
        val groups = splitOnTypeGroups(voteResultsEntity.votes!!, voteResultTypes)
                .filterValues { it.size >= GROUP_MIN_SIZE }
        val increment = VoteSessionAnalyserResult(
                count = 1,
                voteResultIds = listOf(voteResultsEntity.id)
        )
        groups.forEach { _, delegates ->
            result.entries.forEach {
                val intersection = it.key.intersect(delegates)
                if (intersection.size >= GROUP_MIN_SIZE && intersection != delegates) {
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
        result = result.filterValues {
            (it.count + recordsLeft) >= threshold
        }
    }
    return result
}
