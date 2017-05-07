package org.challenge.qualification.votes.scanner

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.Closeable
import java.util.*

class PdfLinesScanner(
        private val reader: PdfReader
): Closeable {
    override fun close() = reader.close()

    private var currentPage = 1
    private var currentScanner: Scanner? = null

    fun hasNextLine() = currentPage <= reader.numberOfPages
            || currentScanner?.hasNextLine()?:false

    private fun nextPage() {
        currentScanner = Scanner(PdfTextExtractor.getTextFromPage(reader, currentPage))
        currentPage++
    }

    fun nextLine() = withScanner { it.nextLine() }

    private fun withScanner(method: (Scanner) -> String): String? {
        if (currentScanner?.hasNextLine()?:false) {
            return method(currentScanner!!)
        } else if(currentPage <= reader.numberOfPages) {
            nextPage()
            return method(currentScanner!!)
        }
        return null
    }
}