package org.challenge.qualification.services

import com.itextpdf.text.pdf.PdfReader
import net.lingala.zip4j.core.ZipFile
import org.apache.tomcat.util.http.fileupload.FileUtils
import org.challenge.qualification.extensions.debug
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files

/**
 */
@Service
class DataSetService {
    companion object{
        val log: Logger = LoggerFactory.getLogger(DataSetService::class.java.canonicalName)
    }
    fun parseDataSet(file: MultipartFile) {
        val folder = Files.createTempDirectory("qualification").toFile()
        log.debug {
            "Temp folder path ${folder.absolutePath}"
        }
        log.debug {
            "Temp zip path ${File(folder, "temp.zip").toPath()}"
        }
        Files.copy(file.inputStream, File(folder, "temp.zip").toPath())
        val filesFolder = File(folder, "files")
        val zipFile = ZipFile(File(folder, "temp.zip"))
        zipFile.extractAll(filesFolder.absolutePath)
        val votesScanner = VoteResultsScanner(PdfReader(filesFolder.listFiles()[0].inputStream()))
        var voteResult = votesScanner.nextVoteResult()
        var prevKeys = voteResult?.votes?.keys
        println("File: ${filesFolder.listFiles()[0].name}")
        do {
            voteResult = votesScanner.nextVoteResult()
            println("Topic: ${voteResult?.topic}")
            val currentKeys = voteResult?.votes?.keys
            currentKeys
                    ?.filter { !(prevKeys?.contains(it)?:true) }
                    ?.forEach { println("[+] $it") }
            prevKeys
                    ?.filter { !(currentKeys?.contains(it)?:true) }
                    ?.forEach { println("[-] $it") }
            prevKeys = currentKeys
        }while (voteResult != null)
        filesFolder.listFiles().drop(1).forEach {
            println("File: ${it.name}")
            val votesScanner = VoteResultsScanner(PdfReader(it.inputStream()))
            do {
                voteResult = votesScanner.nextVoteResult()
                println("Topic: ${voteResult?.topic}")
                val currentKeys = voteResult?.votes?.keys
                currentKeys
                        ?.filter { !(prevKeys?.contains(it)?:true) }
                        ?.forEach { println("[+] $it") }
                prevKeys
                        ?.filter { !(currentKeys?.contains(it)?:true) }
                        ?.forEach { println("[-] $it") }
                prevKeys = currentKeys
            }while (voteResult != null)
        }
        FileUtils.deleteDirectory(folder)
    }
}