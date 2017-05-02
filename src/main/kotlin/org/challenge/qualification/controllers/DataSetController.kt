package org.challenge.qualification.controllers

import org.challenge.qualification.services.DataSetService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 */
@RestController
@RequestMapping("/data_set")
class DataSetController(
        val dataSetService: DataSetService
) {
    companion object{
        val log: Logger = LoggerFactory.getLogger(DataSetController::class.java.canonicalName)
    }
    @PostMapping("/upload")
    fun uploadDataSet(@RequestParam("file") file: MultipartFile): String {
        log.info("Uploading dataSet: {} ...", file.originalFilename)
        dataSetService.parseDataSet(file)
        return "temp"
    }
}