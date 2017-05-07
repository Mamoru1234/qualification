package org.challenge.qualification.controllers

import org.challenge.qualification.dao.DelegateDao
import org.challenge.qualification.entity.delegateEntityToDto
import org.challenge.qualification.entity.voteEntityToDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 */
@RestController
@RequestMapping("/delegate")
class DelegateController (
        val delegateDao: DelegateDao
){

    @GetMapping
    fun getAll() = delegateDao.findAll().map(::delegateEntityToDto)

    @GetMapping("/{id}/info")
    fun getById(@PathVariable id: UUID) = delegateEntityToDto(delegateDao.findOne(id))

    @GetMapping("/{id}/votes")
    fun getVotesById(@PathVariable id: UUID) = delegateDao.findOne(id).votes?.map(::voteEntityToDto)
}