package org.challenge.qualification.services

import com.itextpdf.text.pdf.PdfReader
import org.challenge.qualification.dto.SessionInfo
import org.challenge.qualification.dto.VoteResultType
import org.challenge.qualification.dto.VoteResults
import org.challenge.qualification.exceptions.ChallengeQualificationException
import java.io.Closeable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val SESSION_NUMBER_REGEX = Regex("^\\d{1,2}")
val SESSION_DATE_REGEX = Regex("\\d{2}\\.\\d{2}\\.\\d{2}$")
val SESSION_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")

fun getSessionNumber(line: String) = SESSION_NUMBER_REGEX.find(line)
        ?.value
        ?.toInt()
        ?: throw ChallengeQualificationException()

fun getSessionDate(line: String) = SESSION_DATE_REGEX.find(line)
        ?.value
        ?.let { LocalDate.parse(it, SESSION_DATE_FORMAT) }
        ?: throw ChallengeQualificationException()

fun readSessionInfo(pdfLinesScanner: PdfLinesScanner): SessionInfo? {
    while (pdfLinesScanner.hasNextLine()) {
        val line = pdfLinesScanner.nextLine()
        if (line?.contains("чергова сесія")?:false) {
            line!!
            return SessionInfo(
                    sessionNumber = getSessionNumber(line),
                    sessionDate = getSessionDate(line)
            )
        }
    }
    return null
}

fun readTableHeader(pdfLinesScanner: PdfLinesScanner) {
    while (pdfLinesScanner.hasNextLine()) {
        val line = pdfLinesScanner.nextLine()
        if (line?.toLowerCase() == "п/п по-батькові депутата п/п по-батькові депутата") {
            return
        }
    }
}

fun parseVoteResultType(words:List<String>):Pair<VoteResultType, List<String>>? {
    when (words.last().toLowerCase()) {
        "за" -> return VoteResultType.POSITIVE to words.dropLast(1)
        "проти" -> return VoteResultType.NEGATIVE to words.dropLast(1)
        "відсутній" -> return VoteResultType.ABSENT to words.dropLast(1)
        "голосував" -> return VoteResultType.IGNORED to words.dropLast(2)
        "утримався" -> return VoteResultType.ABSTAINED to words.dropLast(1)
        else -> return null
    }
}

val voteResultWords = listOf("За", "Відсутній", "голосував")

fun readVotes(pdfLinesScanner: PdfLinesScanner): Map<String, VoteResultType> {
    var votes = emptyMap<String, VoteResultType>()
    var unParsedWords = emptyList<String>()
    while (pdfLinesScanner.hasNextLine()) {
        val line = pdfLinesScanner.nextLine() ?: break
        if (line.toUpperCase() == "ПІДСУМКИ ГОЛОСУВАННЯ") {
            break
        }
        var words = line.split(Regex("[\\s\\xa0]+")).filter { it != "" }
        while (words.isNotEmpty()) {
            val parseResult = parseVoteResultType(words)
            if (parseResult == null) {
                unParsedWords += words
                break
            }
            val (voteType, buffer) = parseResult
            words = buffer
            val numberInd = words.indexOfLast { it.matches(Regex("\\d{1,2}")) }
            var nameWords = words.subList(numberInd + 1, words.size)
            val neededWords = 3 - nameWords.size
            nameWords = unParsedWords.subList(0, neededWords) + nameWords
            unParsedWords = unParsedWords.drop(neededWords)
            votes += nameWords.joinToString(" ") to voteType
            words = words.dropLast(words.size - numberInd)
        }
    }
    if (unParsedWords.isNotEmpty()) {
        println(unParsedWords)
        throw ChallengeQualificationException("UnParsed words left")
    }
    return votes
}

fun readTopic(pdfLinesScanner: PdfLinesScanner): String? {
    var topicLines: List<String>? = null
    while (pdfLinesScanner.hasNextLine()) {
        val line = pdfLinesScanner.nextLine()
        if (line?.toLowerCase()?.contains("результат поіменного голосування:")?:false) {
            topicLines = emptyList<String>()
            continue
        }
        if (line?.toLowerCase()?.matches(Regex(".*прізвище, ім'я та.* прізвище, ім'я та.*"))?:false && topicLines != null) {
            val numberInd = topicLines.indexOfLast {
                it.contains("№")
            }
            return topicLines.dropLast(topicLines.size - numberInd).joinToString("")
        }
        if (topicLines != null && line != null) {
            topicLines += line
        }
    }
    return null
}

/**
 */
class VoteResultsScanner(
        reader:PdfReader
): Closeable {
    private val pdfScanner = PdfLinesScanner(reader)

    fun nextVoteResult(): VoteResults? {
        val topic = readTopic(pdfScanner) ?: return null
//        table structure is hardcoded no need to read headers correctly
//        first header line is used as terminator for topic read
        readTableHeader(pdfScanner)
        val votes = readVotes(pdfScanner)
        return VoteResults(
                topic = topic,
                votes = votes
        )
    }

    fun getSessionInfo(): SessionInfo? {
        return readSessionInfo(pdfScanner)
    }

    override fun close() = pdfScanner.close()
}