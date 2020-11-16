package pro.felixo.csv

import pro.felixo.csv.internal.PeekingReader
import java.io.IOException
import java.io.Reader

/**
 * Reads CSV-formatted data from `source` using the parameters specified by [format].
 *
 * Use this class only if you require lower-level control over the CSV reading process. For most use cases, the
 * pre-defined convenience methods, such as [readCsv], should be sufficient.
 */
class CsvReader(
    source: Reader,
    private val format: CsvFormat = CsvFormat()
): AutoCloseable {
    private val valueTerminators = arrayOf(format.delimiter, '\r', '\n', null)

    private val buffer = StringBuffer()
    private val reader = PeekingReader(source)
    private var rowNumber = 0

    /**
     * Returns one row of values as a sequence, which may be iterated over only once.
     * After exhausting the sequence, the underlying Reader's position will be at the beginning of the subsequent row.
     * This method must not be invoked again until the previously returned sequence has been exhausted.
     *
     * An empty line is considered to be a row with one empty value.
     *
     * Returns `null` if the last row has already been read.
     *
     * Reading from the returned sequence may cause a [CsvException] in case the input data is malformed, or
     * any exception (most notably [IOException]) thrown by the underlying [Reader].
     */
    fun readRow(): Sequence<String>? = reader.next?.let {
        sequence {
            while (true) {
                buffer.setLength(0)
                when (reader.next) {
                    format.quote -> readQuotedValue()
                    else -> readUnquotedValue()
                }
                yield(buffer.toString())
                if (reader.next == format.delimiter)
                    reader.read()
                else if (skipRowTerminator())
                    break
            }
            rowNumber++
        }
    }

    private fun readQuotedValue() {
        reader.read()  // skip initial quote
        while (true) {
            val char = reader.read() ?: throw CsvException("Unexpected end of file in quoted value", rowNumber)
            if (char == format.escape && reader.next == format.quote || format.escape != format.quote)
                buffer.append(
                    reader.read() ?: throw CsvException("Unexpected end of file within escape sequence", rowNumber)
                )
            else if (char == format.quote)
                break
            else
                buffer.append(char)
        }
        if (!isValueTerminator())
            throw CsvException("Quoted value not terminated after ending quote", rowNumber)
    }

    private fun readUnquotedValue() {
        while (!isValueTerminator())
            buffer.append(reader.read()!!)
    }

    private fun isValueTerminator(): Boolean = reader.next in valueTerminators

    private fun skipRowTerminator(): Boolean = when (reader.next) {
        null -> true
        '\n' -> {
            reader.read()
            true
        }
        '\r' -> {
            reader.read()
            if (reader.next == '\n')
                reader.read()
            true
        }
        else -> false
    }

    /**
     * Implements [AutoCloseable] and closes the underlying [Reader]. Only call this method after you are done
     * reading data from the [Sequence] returned by [readRow].
     */
    override fun close() = reader.close()
}
