package org.challenge.qualification.controllers

import org.challenge.qualification.dao.DataSetDao
import org.challenge.qualification.dao.VoteResultsDao
import org.challenge.qualification.dao.VoteSessionDao
import org.challenge.qualification.dto.DataSetDto
import org.challenge.qualification.entity.dataSetEntityToDto
import org.challenge.qualification.entity.voteEntityToDto
import org.challenge.qualification.entity.voteResultsEntityToDto
import org.challenge.qualification.entity.voteSessionEntityToDto
import org.challenge.qualification.services.DataSetService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

/**
 */
@RestController
    @RequestMapping("/data_set")
class DataSetController(
        val dataSetDao: DataSetDao,
        val voteResultsDao: VoteResultsDao,
        val voteSessionDao: VoteSessionDao,
        val dataSetService: DataSetService
) {
    companion object{
        val log: Logger = LoggerFactory.getLogger(DataSetController::class.java.canonicalName)
    }
    @PostMapping("/upload")
    fun uploadDataSet(@RequestParam("file") file: MultipartFile): DataSetDto {
        log.info("Uploading dataSet: {} ...", file.originalFilename)
        val result = dataSetService.uploadDataSet(file.inputStream)
        log.info("DataSet uploaded")
        return dataSetEntityToDto(result)
    }

    @GetMapping
    fun getAll() = dataSetDao.findAll().map(::dataSetEntityToDto)

    @GetMapping("{id}/sessions")
    fun getVoteSessions (@PathVariable id: UUID) = dataSetDao.findOne(id).sessions
            ?.map(::voteSessionEntityToDto)

    @GetMapping("session/{id}/vote_results")
    fun getSession(@PathVariable id: UUID) = voteSessionDao.findOne(id).voteResults
            ?.map(::voteResultsEntityToDto)
    @GetMapping("vote_results/{id}/votes")
    fun getVotes(@PathVariable id: UUID) = voteResultsDao.findOne(id).votes
            ?.map(::voteEntityToDto)
}