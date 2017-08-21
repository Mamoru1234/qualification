package org.challenge.qualification.services

import com.itextpdf.text.pdf.PdfReader
import net.lingala.zip4j.core.ZipFile
import org.apache.tomcat.util.http.fileupload.FileUtils
import org.challenge.qualification.daos.*
import org.challenge.qualification.entities.*
import org.challenge.qualification.extensions.debug
import org.challenge.qualification.votes.scanner.VoteResults
import org.challenge.qualification.votes.scanner.VoteResultsScanner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import javax.transaction.Transactional

/**
 */
@Service
open class DataSetService(
        val dataSetDao: DataSetDao,
        val voteSessionDao: VoteSessionDao,
        val voteResultsDao: VoteResultsDao,
        val delegateDao: DelegateDao,
        val voteDao: VoteDao
){
    companion object{
        val log: Logger = LoggerFactory.getLogger(DataSetService::class.java.canonicalName)
    }

    @Transactional
    open fun uploadDataSet(dataSetInputStream: InputStream): DataSetEntity {
        val dataSet = DataSetEntity()
        dataSetDao.save(dataSet)
        val folder = Files.createTempDirectory("qualification").toFile()
        log.debug {
            "Temp folder path ${folder.absolutePath}"
        }
        log.debug {
            "Temp zip path ${File(folder, "temp.zip").toPath()}"
        }
        Files.copy(dataSetInputStream, File(folder, "temp.zip").toPath())
        val filesFolder = File(folder, "files")
        val zipFile = ZipFile(File(folder, "temp.zip"))
        zipFile.extractAll(filesFolder.absolutePath)
        filesFolder.listFiles().forEach {
            log.debug {
                "File: ${it.name}"
            }
            VoteResultsScanner(PdfReader(it.inputStream())).use { votesScanner ->
                val sessionInfo = votesScanner.getSessionInfo()!!
                val voteSession = VoteSessionEntity(
                        sessionNumber = sessionInfo.sessionNumber,
                        sessionDate = sessionInfo.sessionDate,
                        dataSet = dataSet
                )
                voteSessionDao.save(voteSession)
                do {
                    val voteResult = votesScanner.nextVoteResult()
                            ?.let {
                                insertVoteResults(it, voteSession)
                            }
                }while (voteResult != null)
            }
        }
        FileUtils.deleteDirectory(folder)
        return dataSet
    }

    private fun insertVoteResults(voteResult: VoteResults, voteSession: VoteSessionEntity) {
        val delegateEntitiesInDB = delegateDao.findAll()
        var newDelegateEntities = emptyList<DelegateEntity>()
        val voteResultEntity = voteResultsDao.save(VoteResultsEntity(
                topic = voteResult.topic,
                metaData = voteResult.voteMetaData,
                voteSession = voteSession
        ))
        val voteEntities = voteResult.votes.map { (delegate, voteResult) ->
            var delegateEntity = delegateEntitiesInDB.find(findByDelegate(delegate))
            if (delegateEntity == null) {
                log.debug {
                    "New delegate $delegate"
                }
                delegateEntity = delegateEntityFrom(delegate)
                newDelegateEntities += delegateEntity
            }
            VoteEntity(
                    result = voteResult,
                    delegate = delegateEntity,
                    voteResults = voteResultEntity
            )
        }
        delegateDao.save(newDelegateEntities)
        voteDao.save(voteEntities)
        log.debug {
            "Topic: ${voteResult.topic}"
        }
    }
}