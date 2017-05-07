package org.challenge.qualification.controllers

import org.challenge.qualification.daos.VoteSessionDao
import org.challenge.qualification.entities.voteResultsEntityToDto
import org.challenge.qualification.entities.voteSessionEntityToDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 */
@RestController
@RequestMapping("/session")
class VoteSessionController(
        val voteSessionDao: VoteSessionDao
) {

    @GetMapping
    fun getAll() = voteSessionDao.findAll().map(::voteSessionEntityToDto)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID) = voteSessionEntityToDto(voteSessionDao.findOne(id))

    @GetMapping("/{id}/results")
    fun getVoteResults(@PathVariable id: UUID) = voteSessionDao.findOne(id).voteResults
            ?.map(::voteResultsEntityToDto)
}