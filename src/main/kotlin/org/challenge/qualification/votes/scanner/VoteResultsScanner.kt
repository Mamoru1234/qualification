package org.challenge.qualification.votes.scanner

val SPACE = "[\\s\\xa0]+"
val SESSION_NUMBER_REGEX = Regex("^\\d{1,2}")
val SESSION_DATE_REGEX = Regex("\\d{2}\\.\\d{2}\\.\\d{2}$")
val SESSION_DATE_FORMAT: java.time.format.DateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy")

fun getSessionNumber(line: String) = org.challenge.qualification.votes.scanner.SESSION_NUMBER_REGEX.find(line)
        ?.value
        ?.toInt()
        ?: throw org.challenge.qualification.exceptions.ChallengeQualificationException()

fun getSessionDate(line: String) = org.challenge.qualification.votes.scanner.SESSION_DATE_REGEX.find(line)
        ?.value
        ?.let { java.time.LocalDate.parse(it, org.challenge.qualification.votes.scanner.SESSION_DATE_FORMAT) }
        ?: throw org.challenge.qualification.exceptions.ChallengeQualificationException()

fun readSessionInfo(pdfLinesScanner: PdfLinesScanner): org.challenge.qualification.dtos.SessionInfo? {
    while (pdfLinesScanner.hasNextLine()) {
        val line = pdfLinesScanner.nextLine()
        if (line
                ?.toLowerCase()
                ?.matches(Regex("(.*чергова${org.challenge.qualification.votes.scanner.SPACE}сесія.*)|(.*чергової${org.challenge.qualification.votes.scanner.SPACE}сесії.*)"))?:false) {
            line!!
            return org.challenge.qualification.dtos.SessionInfo(
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
        if (line?.toLowerCase()
                ?.matches(Regex("п/п${org.challenge.qualification.votes.scanner.SPACE}по-батькові${org.challenge.qualification.votes.scanner.SPACE}депутата${org.challenge.qualification.votes.scanner.SPACE}п/п${org.challenge.qualification.votes.scanner.SPACE}по-батькові${org.challenge.qualification.votes.scanner.SPACE}депутата"))
                ?:false) {
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

fun readVotes(pdfLinesScanner: PdfLinesScanner): Map<Delegate, VoteResultType> {
    var votes = emptyMap<Delegate, VoteResultType>()
    var unParsedWords = emptyList<String>()
    while (pdfLinesScanner.hasNextLine()) {
        val line = pdfLinesScanner.nextLine() ?: break
        if (line.toUpperCase().matches(Regex("ПІДСУМКИ${org.challenge.qualification.votes.scanner.SPACE}ГОЛОСУВАННЯ"))) {
            break
        }
        var words = line.split(Regex(org.challenge.qualification.votes.scanner.SPACE)).filter { it != "" }
        while (words.isNotEmpty()) {
            val parseResult = org.challenge.qualification.votes.scanner.parseVoteResultType(words)
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
            val delegate = Delegate(
                    lastName = nameWords[0],
                    firstName = nameWords[1],
                    middleName = nameWords[2]
            )
            votes += delegate to voteType
            words = words.dropLast(words.size - numberInd)
        }
    }
    if (unParsedWords.isNotEmpty()) {
        println(unParsedWords)
        throw org.challenge.qualification.exceptions.ChallengeQualificationException("UnParsed words left")
    }
    return votes
}

fun readTopic(pdfLinesScanner: PdfLinesScanner): Pair<String,String>? {
    var topicLines: List<String>? = null
    while (pdfLinesScanner.hasNextLine()) {
        val line = pdfLinesScanner.nextLine()
        if (line?.toLowerCase()?.matches(Regex("результат${org.challenge.qualification.votes.scanner.SPACE}поіменного${org.challenge.qualification.votes.scanner.SPACE}голосування:"))?:false) {
            topicLines = emptyList<String>()
            continue
        }
        if (line?.toLowerCase()
                ?.matches(Regex(".*прізвище,${org.challenge.qualification.votes.scanner.SPACE}ім'я${org.challenge.qualification.votes.scanner.SPACE}та.*${org.challenge.qualification.votes.scanner.SPACE}прізвище,${org.challenge.qualification.votes.scanner.SPACE}ім'я${org.challenge.qualification.votes.scanner.SPACE}та.*"))
                ?:false && topicLines != null) {
            val numberInd = topicLines.indexOfLast { it.contains("№") }
            val topic = topicLines.dropLast(topicLines.size - numberInd).joinToString("")
            return topic to topicLines[numberInd]
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
        reader: com.itextpdf.text.pdf.PdfReader
): java.io.Closeable {
    private val pdfScanner = PdfLinesScanner(reader)

    fun nextVoteResult(): VoteResults? {
        val (topic, voteMetaData) = org.challenge.qualification.votes.scanner.readTopic(pdfScanner) ?: return null
//        table structure is hardcoded no need to read headers correctly
//        first header line is used as terminator for topic read
        org.challenge.qualification.votes.scanner.readTableHeader(pdfScanner)
        val votes = org.challenge.qualification.votes.scanner.readVotes(pdfScanner)
        return VoteResults(
                topic = topic,
                voteMetaData = voteMetaData,
                votes = votes
        )
    }

    fun getSessionInfo(): org.challenge.qualification.dtos.SessionInfo? {
        return org.challenge.qualification.votes.scanner.readSessionInfo(pdfScanner)
    }

    override fun close() = pdfScanner.close()
}