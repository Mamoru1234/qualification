package org.challenge.qualification.controllers

import org.challenge.qualification.daos.VoteResultsDao
import org.challenge.qualification.entities.voteEntityToDto
import org.challenge.qualification.entities.voteResultsEntityToDto
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 */
@RestController
@RequestMapping("/vote_results")
class VoteResultsController (
        val voteResultsDao: VoteResultsDao
){
    @GetMapping
    fun getAll() = voteResultsDao.findAll().map(::voteResultsEntityToDto)

    @GetMapping("/pagination")
    fun getPage(
            @RequestParam(value = "page", required = false) pageNumber: Int?,
            @RequestParam(value = "limit", required = false) limit: Int?
    ) = voteResultsDao.findAll(PageRequest(pageNumber?:0, limit?:10)).map(::voteResultsEntityToDto)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID) = voteResultsEntityToDto(voteResultsDao.findOne(id))

    @GetMapping("/{id}/votes")
    fun getVotes(@PathVariable id: UUID) = voteResultsDao.findOne(id).votes
            ?.map(::voteEntityToDto)
}