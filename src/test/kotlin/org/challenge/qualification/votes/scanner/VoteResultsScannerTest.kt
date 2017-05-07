package org.challenge.qualification.votes.scanner

import com.itextpdf.text.pdf.PdfReader
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.matches
import org.junit.Test
import java.io.FileInputStream
import java.time.LocalDate

/**
 */
class VoteResultsScannerTest {
    fun scannerTestRound(scanner: VoteResultsScanner, topics: List<String?> = emptyList()) {
        var voteResults = scanner.nextVoteResult()
        voteResults!!
        val prevKeys = voteResults.votes.keys
        assertThat(voteResults.topic, matches(Regex("Про${SPACE}затвердження${SPACE}порядку${SPACE}денного")))
        assertThat(voteResults.votes.keys.size, equalTo(37))
        var topicInd = 0
        do {
            voteResults = scanner.nextVoteResult()
            if (voteResults != null) {
                assertThat(voteResults.votes.keys, equalTo(prevKeys))
                val topic = topics.getOrNull(topicInd)
                if (topic != null) {
                    assertThat(voteResults.topic, equalTo(topic))
                }
                topicInd++
            }
        }while (voteResults != null)
    }
    @Test
    fun nextVoteResult() {
        val topics = listOf(
                null,
                null,
                "Про затвердження порядку денного",
                "1. Про надання дозволу на передачу комунального майна територіальної громади м.Бровари.",
                "1. Про надання дозволу на передачу комунального майна територіальної громади м.Бровари.",
                "2. Про надання дозволу на списання комунального майна територіальної громади м.Бровари.",
                "2. Про надання дозволу на списання комунального майна територіальної громади м.Бровари.",
                "3. Про надання дозволу на передачу внутрішньобудинкових мереж та обладнання з балансу КП «Броваритепловодоенергія».",
                null,
                "3. Про надання дозволу на передачу внутрішньобудинкових мереж та обладнання з балансу КП «Броваритепловодоенергія».",
                "4. Про списання багатоквартирних будинків (гуртожитків) з балансу комунальних підприємств Броварської міської ради."
        )
        val reader = PdfReader(FileInputStream(javaClass.getResource("/4.8.2016.pdf").path))
        val scanner = VoteResultsScanner(reader)
        val info = scanner.getSessionInfo()
        val date = LocalDate.of(2016, 8, 4)
        assertThat(info, equalTo(SessionInfo(17, date)))
        scannerTestRound(scanner, topics)
        scanner.close()
    }
    @Test
    fun nextVoteResult1() {
        val reader = PdfReader(FileInputStream(javaClass.getResource("/3.11.2016.pdf").path))
        val scanner = VoteResultsScanner(reader)
        val info = scanner.getSessionInfo()
        val date = LocalDate.of(2016, 11, 3)
        assertThat(info, equalTo(SessionInfo(20, date)))
        scannerTestRound(scanner)
        scanner.close()
    }

    @Test
    fun nextVoteResult2() {
        val reader = PdfReader(FileInputStream(javaClass.getResource("/3.4.2017.pdf").path))
        val scanner = VoteResultsScanner(reader)
        val info = scanner.getSessionInfo()
        val date = LocalDate.of(2017, 4, 3)
        assertThat(info, equalTo(SessionInfo(27, date)))
        scannerTestRound(scanner)
        scanner.close()
    }

    @Test
    fun nextVoteResult3() {
        val reader = PdfReader(FileInputStream(javaClass.getResource("/18.10.2016.pdf").path))
        val scanner = VoteResultsScanner(reader)
        val info = scanner.getSessionInfo()
        val date = LocalDate.of(2016, 10, 18)
        assertThat(info, equalTo(SessionInfo(19, date)))
        scannerTestRound(scanner)
        scanner.close()
    }
}