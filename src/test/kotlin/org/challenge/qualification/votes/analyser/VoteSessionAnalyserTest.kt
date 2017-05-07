package org.challenge.qualification.votes.analyser

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import org.challenge.qualification.entities.DelegateEntity
import org.challenge.qualification.entities.VoteEntity
import org.challenge.qualification.entities.VoteResultsEntity
import org.challenge.qualification.votes.scanner.VoteResultType
import org.challenge.qualification.votes.scanner.VoteResultType.NEGATIVE
import org.challenge.qualification.votes.scanner.VoteResultType.POSITIVE
import org.junit.Test
import java.util.*

/**
 */
class VoteSessionAnalyserTest {
    fun generateDelegates(count: Int = 5): List<DelegateEntity> {
        var result: List<DelegateEntity> = emptyList()
        for (i in 1..count) {
            result += DelegateEntity()
        }
        return result
    }
    fun generateVoteSessionEntity(
            config: Map<UUID, List<Pair<VoteResultType, Int>>>,
            delegates: List<DelegateEntity>
    ): List<VoteResultsEntity> {
        return config.entries.map {
                    VoteResultsEntity(
                            id = it.key,
                            votes = it.value.map { (result, delegateInd) ->
                                VoteEntity(
                                        result = result,
                                        delegate = delegates[delegateInd]
                                )
                            }
                    )
                }.toList()
    }
    @Test
    fun testVoteAnalyseSimplestCase() {
        val delegates = generateDelegates()
        val data = generateVoteSessionEntity(mapOf(
                UUID.randomUUID() to listOf(
                        POSITIVE to 0,
                        NEGATIVE to 1
                )
        ), delegates)
        val result = analyseVoteSession(data)
        assertThat(result.keys, hasSize(equalTo(0)))
    }
    @Test
    fun testVoteAnalyseNewDelegate() {
        val delegates = generateDelegates()
        val topics = (1..2).map { UUID.randomUUID() }
        val data = generateVoteSessionEntity(mapOf(
                topics[0] to listOf(
                        POSITIVE to 0,
                        NEGATIVE to 1
                ),
                topics[1] to listOf(
                        POSITIVE to 2,
                        POSITIVE to 1
                )
        ), delegates)
        val result = analyseVoteSession(data)
        assertThat(result[setOf(delegates[1], delegates[2])]?.count, equalTo(1))
        assertThat(result[setOf(delegates[1], delegates[2])]?.voteResultIds, equalTo(listOf(topics[1])))
        assertThat(result.keys, hasSize(equalTo(1)))
    }

    @Test
    fun testVoteAnalyseThroughIntersection() {
        val delegates = generateDelegates()
        val topics = (1..3).map { UUID.randomUUID() }
        val data = generateVoteSessionEntity(mapOf(
                topics[0] to listOf(
                        NEGATIVE to 0,
                        POSITIVE to 1,
                        POSITIVE to 2,
                        POSITIVE to 3,
                        NEGATIVE to 4
                ),
                topics[1] to listOf(
                        NEGATIVE to 0,
                        NEGATIVE to 1,
                        POSITIVE to 2,
                        POSITIVE to 3,
                        POSITIVE to 4
                ),
                topics[2] to listOf(
                        NEGATIVE to 0,
                        NEGATIVE to 1,
                        POSITIVE to 2,
                        POSITIVE to 3,
                        NEGATIVE to 4
                )
        ), delegates)
        var result = analyseVoteSession(data)
        assertThat(result[setOf(delegates[0], delegates[4])]?.count, equalTo(2))
        assertThat(result[setOf(delegates[0], delegates[1])]?.count, equalTo(2))
        assertThat(result[setOf(delegates[2], delegates[3])]?.count, equalTo(3))
        assertThat(result[setOf(delegates[2], delegates[3])]?.voteResultIds, equalTo(topics))
        assertThat(result.keys, hasSize(equalTo(3)))
        result = analyseVoteSession(data, groupThreshold = 10)
        assertThat(result.keys, hasSize(equalTo(6)))
    }
}