package org.challenge.qualification.controllers

import org.challenge.qualification.daos.DelegateDao
import org.challenge.qualification.entities.delegateEntityToDto
import org.challenge.qualification.entities.voteEntityToDto
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
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

    @GetMapping("/pagination")
    fun getPage(
            @RequestParam(value = "page", required = false) pageNumber: Int?,
            @RequestParam(value = "limit", required = false) limit: Int?
    ) = delegateDao.findAll(PageRequest(pageNumber?:0, limit?:10)).map(::delegateEntityToDto)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID) = delegateEntityToDto(delegateDao.findOne(id))

    @GetMapping("/{id}/votes")
    fun getVotesById(@PathVariable id: UUID) = delegateDao.findOne(id).votes?.map(::voteEntityToDto)
}