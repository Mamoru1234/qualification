package org.challenge.qualification.controllers

import org.challenge.qualification.daos.VoteResultsDao
import org.challenge.qualification.daos.VoteSessionDao
import org.challenge.qualification.entities.delegateEntityToDto
import org.challenge.qualification.entities.voteResultsEntityToDto
import org.challenge.qualification.entities.voteSessionEntityToDto
import org.challenge.qualification.exceptions.ChallengeQualificationException
import org.challenge.qualification.votes.analyser.analyseVoteSession
import org.challenge.qualification.votes.scanner.VoteResultType
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 */
@RestController
@RequestMapping("/session")
class VoteSessionController(
        private val voteSessionDao: VoteSessionDao,
        private val voteResultsDao: VoteResultsDao
) {

    @GetMapping
    fun getAll() = voteSessionDao.findAll().map(::voteSessionEntityToDto)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID) = voteSessionEntityToDto(voteSessionDao.findOne(id))

    @GetMapping("/{id}/results")
    fun getVoteResults(
            @PathVariable id: UUID,
            @RequestParam(name = "page", required = false) pageNumber: Int?,
            @RequestParam(name = "limit", required = false) limit: Int?
    ) = voteResultsDao.findByVoteSessionId(id, PageRequest(pageNumber?:0, limit?:10))
            .map(::voteResultsEntityToDto)

    @GetMapping("/{id}/analyse")
    fun getVoteResultsAnalys(
            @PathVariable id: UUID,
            @RequestParam(name = "page", required = false) pageNumber: Int?,
            @RequestParam(name = "limit", required = false) limit: Int?,
            @RequestParam(name = "accept", required = false) accept: String?,
            @RequestParam(name = "group_threshold", required = false) groupThreshold: Int?
    ) = voteResultsDao.findByVoteSessionId(id, PageRequest(pageNumber?:0, limit?:10)).content
            .let {
                try {
                    val voteTypes = accept?.split(",")
                            ?.map { VoteResultType.valueOf(it) }
                            ?:VoteResultType.values().toList()
                    analyseVoteSession(
                            voteResults = it,
                            voteResultTypes = voteTypes,
                            groupThreshold = groupThreshold?:1
                    )
                } catch (e: IllegalArgumentException) {
                    throw ChallengeQualificationException("Wrong accept type")
                }
            }.entries
            .map {
               mapOf(
                       "delegates" to it.key.map(::delegateEntityToDto),
                       "results" to it.value
               )
            }

}