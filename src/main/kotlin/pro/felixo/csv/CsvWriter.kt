package pro.felixo.csv

import java.io.IOException
import java.io.Writer

/**
 * Writes CSV-formatted data to [target] using the parameters specified by [format].
 *
 * Use this class only if you require lower-level control over the CSV writing process. For most use cases, the
 * pre-defined convenience methods, such as [writeCsvAndClose], should be sufficient.
 */
class CsvWriter(
    private val target: Writer,
    private val format: CsvFormat = CsvFormat()
): AutoCloseable {
    private val quoteString = format.quote.toString()
    private val escapedQuoteString = "${format.escape}${format.quote}"
    private val quoteInt = format.quote.toInt()
    private val delimiterInt = format.delimiter.toInt()

    /**
     * Writes one row of values to the underlying [Writer] using [format].
     *
     * Any exceptions thrown by the underlying [Writer], most notably [IOException]s, will be passed through.
     */
    fun writeRow(values: Iterable<String>) {
        val iterator = values.iterator()
        if (iterator.hasNext())
            writeValue(iterator.next())
        while (iterator.hasNext()) {
            target.write(delimiterInt)
            writeValue(iterator.next())
        }
        target.write(format.terminator)
    }

    private fun writeValue(value: String) {
        if (value.any { it in format.specialChars })
            writeQuotedValue(value)
        else
            writeUnquotedValue(value)
    }

    private fun writeQuotedValue(value: String) {
        target.write(quoteInt)
        target.write(value.replace(quoteString, escapedQuoteString))
        target.write(quoteInt)
    }

    private fun writeUnquotedValue(value: String) = target.write(value)

    /**
     * Implements [AutoCloseable] and closes the underlying [Writer]. Only call this method after you are done
     * writing data using [writeRow].
     */
    override fun close() = target.close()
}
