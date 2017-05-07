package org.challenge.qualification.controllers

import org.challenge.qualification.daos.DataSetDao
import org.challenge.qualification.dtos.DataSetDto
import org.challenge.qualification.entities.dataSetEntityToDto
import org.challenge.qualification.entities.voteSessionEntityToDto
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

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID) = dataSetEntityToDto(dataSetDao.findOne(id))

    @GetMapping("/{id}/sessions")
    fun getVoteSessions (@PathVariable id: UUID) = dataSetDao.findOne(id).sessions
            ?.map(::voteSessionEntityToDto)
}