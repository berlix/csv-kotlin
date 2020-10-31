package pro.felixo.csv

import java.io.*
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

/**
 * Reads all CSV data from the [String] as a [Sequence] of rows.
 *
 * Reading from the returned [Sequence] may cause a [CsvException] in case the input data is malformed.
 */
fun String.readCsv(format: CsvFormat = CsvFormat()) = reader().readCsv(format)

/**
 * Reads all CSV data from the [Reader] as a [Sequence] of rows.
 *
 * The [Reader] will be closed upon exhaustion of the returned [Sequence].
 *
 * Reading from the returned [Sequence] may cause a [CsvException] in case the input data is malformed, or
 * any exception (most notably [IOException]) thrown by the underlying [Reader].
 *
 * Note that, depending on your data source, reading from a [BufferedReader] may improve performance.
 */
fun Reader.readCsv(format: CsvFormat = CsvFormat()): Sequence<List<String>> = sequence {
    CsvReader(this@readCsv, format).use { csvReader ->
        csvReader.readRows().forEach { yield(it) }
    }
}

/**
 * Reads all CSV data from the [File] using the given [charset] as a [Sequence] of rows.
 *
 * Reading from the returned [Sequence] may cause a [CsvException] in case the input data is malformed, or
 * any exception (most notably [IOException]) thrown by the underlying [Reader].
 */
fun File.readCsv(format: CsvFormat = CsvFormat(), charset: Charset = Charsets.UTF_8) = bufferedReader(charset).readCsv(format)

/**
 * Reads all CSV data from the file at the [Path] using the given [charset] as a [Sequence] of rows.
 *
 * Reading from the returned [Sequence] may cause a [CsvException] in case the input data is malformed, or
 * any exception (most notably [IOException]) thrown by the underlying [Reader].
 */
fun Path.readCsv(format: CsvFormat = CsvFormat(), charset: Charset = Charsets.UTF_8) =
    Files.newBufferedReader(this, charset).readCsv(format)

/**
 * Reads all CSV data from the [InputStream] using the given [charset] as a [Sequence] of rows.
 *
 * The [InputStream] will be closed upon exhaustion of the returned [Sequence].
 *
 * Reading from the returned [Sequence] may cause a [CsvException] in case the input data is malformed, or
 * any exception (most notably [IOException]) thrown by the underlying [Reader].
 */
fun InputStream.readCsv(format: CsvFormat = CsvFormat(), charset: Charset = Charsets.UTF_8) = bufferedReader(charset).readCsv(format)

/**
 * Writes the given rows to the [Writer] using the given [CsvFormat], without closing the [Writer].
 *
 * Note that, depending on your data sink, writing to a [BufferedWriter] may improve performance.
 */
fun Writer.writeCsv(rows: Sequence<Iterable<String>>, format: CsvFormat = CsvFormat()) =
    CsvWriter(this, format).writeRows(rows)

/**
 * Writes the given rows to the [Writer] using the given [CsvFormat], without closing the [Writer].
 *
 * Note that, depending on your data sink, writing to a [BufferedWriter] may improve performance.
 */
fun Writer.writeCsv(rows: Iterable<Iterable<String>>, format: CsvFormat = CsvFormat()) =
    writeCsv(rows.asSequence(), format)

/**
 * Writes the given rows to the [Writer] using the given [CsvFormat], and then closes the writer.
 *
 * Note that, depending on your data sink, writing to a [BufferedWriter] may improve performance.
 */
fun Writer.writeCsvAndClose(rows: Sequence<Iterable<String>>, format: CsvFormat = CsvFormat()) =
    CsvWriter(this, format).use { it.writeRows(rows) }

/**
 * Writes the given rows to the [Writer] using the given [CsvFormat], and then closes the writer.
 *
 * Note that, depending on your data sink, writing to a [BufferedWriter] may improve performance.
 */
fun Writer.writeCsvAndClose(rows: Iterable<Iterable<String>>, format: CsvFormat = CsvFormat()) =
    writeCsvAndClose(rows.asSequence(), format)

/**
 * Writes the given rows to the [File] using the given [CsvFormat], overwriting any previous content, and creating
 * a new file if necessary.
 */
fun File.writeCsv(
        rows: Sequence<Iterable<String>>,
        format: CsvFormat = CsvFormat(),
        charset: Charset = Charsets.UTF_8
) =
    bufferedWriter(charset).writeCsvAndClose(rows, format)

/**
 * Writes the given rows to the [File] using the given [CsvFormat], overwriting any previous content, and creating
 * a new file if necessary.
 */
fun File.writeCsv(
        rows: Iterable<Iterable<String>>,
        format: CsvFormat = CsvFormat(),
        charset: Charset = Charsets.UTF_8
) =
    writeCsv(rows.asSequence(), format, charset)

/**
 * Writes the given rows to the file at the [Path] using the given [CsvFormat], overwriting any previous content,
 * and creating a new file if necessary.
 */
fun Path.writeCsv(
        rows: Sequence<Iterable<String>>,
        format: CsvFormat = CsvFormat(),
        charset: Charset = Charsets.UTF_8
) =
    Files.newBufferedWriter(this, charset).writeCsvAndClose(rows, format)

/**
 * Writes the given rows to the file at the [Path] using the given [CsvFormat], overwriting any previous content,
 * and creating a new file if necessary.
 */
fun Path.writeCsv(
        rows: Iterable<Iterable<String>>,
        format: CsvFormat = CsvFormat(),
        charset: Charset = Charsets.UTF_8
) =
    writeCsv(rows.asSequence(), format, charset)

/**
 * Writes the given rows to the [OutputStream] using the given [CsvFormat] and [Charset], and then closes the stream.
 */
fun OutputStream.writeCsvAndClose(
        rows: Sequence<Iterable<String>>,
        format: CsvFormat = CsvFormat(),
        charset: Charset = Charsets.UTF_8
) =
    bufferedWriter(charset).writeCsvAndClose(rows, format)

/**
 * Writes the given rows to the [OutputStream] using the given [CsvFormat] and [Charset], and then closes the stream.
 */
fun OutputStream.writeCsvAndClose(
        rows: Iterable<Iterable<String>>,
        format: CsvFormat = CsvFormat(),
        charset: Charset = Charsets.UTF_8
) =
    writeCsvAndClose(rows.asSequence(), format, charset)

/**
 * Writes the given rows to the [OutputStream] using the given [CsvFormat] and [Charset], without closing the
 * [OutputStream].
 */
fun OutputStream.writeCsv(
        rows: Sequence<Iterable<String>>,
        format: CsvFormat = CsvFormat(),
        charset: Charset = Charsets.UTF_8
) =
    with(bufferedWriter(charset)) {
        writeCsv(rows, format)
        flush()
    }

/**
 * Writes the given rows to the [OutputStream] using the given [CsvFormat] and [Charset], without closing the
 * [OutputStream].
 */
fun OutputStream.writeCsv(
        rows: Iterable<Iterable<String>>,
        format: CsvFormat = CsvFormat(),
        charset: Charset = Charsets.UTF_8
) =
    writeCsv(rows.asSequence(), format, charset)

/**
 * Converts a list of rows with named fields to a sequence of one row of field names and subsequent rows of values.
 * The field names will be the union set of the keys of all input maps.
 * If any row misses a value for a field, the value for that field will be the empty string.
 */
fun List<Map<String, String>>.asRowsWithHeaders(): Sequence<List<String>> =
    asSequence().asRowsWithHeaders(fold(emptySet<String>(), { acc, row -> acc + row.keys }).toList())

/**
 * Converts a list of rows with named fields to a sequence of one row of field names and subsequent rows of values.
 * If any row misses a value for a field, the value for that field will be the empty string.
 */
fun Sequence<Map<String, String>>.asRowsWithHeaders(headers: List<String>): Sequence<List<String>> = sequence {
    yield(headers)
    yieldAll(this@asRowsWithHeaders.map { row -> headers.map { row[it] ?: "" } })
}

/**
 * Converts a list of rows with named fields to a sequence of one row of field names and subsequent rows of values.
 * If any row misses a value for a field, the value for that field will be the empty string.
 */
fun Iterable<Map<String, String>>.asRowsWithHeaders(headers: List<String>): Sequence<List<String>> =
        asSequence().asRowsWithHeaders(headers)

/**
 * Converts a list of rows with named fields to a sequence of one row of field names and subsequent rows of values.
 * If any row misses a value for a field, the value for that field will be the empty string.
 */
fun Sequence<Map<String, String>>.asRowsWithHeaders(vararg headers: String): Sequence<List<String>> =
    asRowsWithHeaders(headers.toList())

/**
 * Converts a list of rows with named fields to a sequence of one row of field names and subsequent rows of values.
 * If any row misses a value for a field, the value for that field will be the empty string.
 */
fun Iterable<Map<String, String>>.asRowsWithHeaders(vararg headers: String): Sequence<List<String>> =
    asSequence().asRowsWithHeaders(*headers)

/**
 * Returns a [Sequence] which does not contain any rows from the input [Sequence] which contain only empty values.
 */
fun Sequence<List<String>>.withoutEmptyRows(): Sequence<List<String>> =
    filter { row -> row.any { value -> value.isNotEmpty() } }

/**
 * Returns a [Sequence] which does not contain any rows from the input [Sequence] which contain only empty values.
 */
fun Iterable<List<String>>.withoutEmptyRows(): Sequence<List<String>> =
    asSequence().withoutEmptyRows()

/**
 * Interprets the first row of the input [Sequence] as field names and the values in the subsequent rows as field
 * values, and returns a [Sequence] of [Map]s of field names to values. If a value row does not have as many values
 * as there are fields, its [Map] will not contain an entry for that field. If a value row has more values than there
 * are fields, then then the extraneous values are ignored.
 */
fun Sequence<List<String>>.asFields(): Sequence<Map<String, String>> {
    val iterator = iterator()
    val headers = if (iterator.hasNext()) iterator.next() else return emptySequence()
    return iterator.asSequence().asFields(headers)
}

/**
 * Interprets the first row of the input [Iterable] as field names and the values in the subsequent rows as field
 * values, and returns a [Sequence] of [Map]s of field names to values. If a value row does not have as many values
 * as there are fields, its [Map] will not contain an entry for that field. If a value row has more values than there
 * are fields, then then the extraneous values are ignored.
 */
fun Iterable<List<String>>.asFields(): Sequence<Map<String, String>> =
    asSequence().asFields()

/**
 * Converts the sequence of rows to a [Sequence] of [Map]s of field names to values. If a row does not have as
 * many values as there are fields, its [Map] will not contain an entry for that field. If a value row has more
 * values than there are fields, then the extraneous values are ignored.
 */
fun Sequence<List<String>>.asFields(fieldNames: List<String>): Sequence<Map<String, String>> =
    map { fieldNames.zip(it).toMap() }

/**
 * Converts the iterable of rows to a [Sequence] of [Map]s of field names to values. If a row does not have as
 * many values as there are fields, its [Map] will not contain an entry for that field. If a value row has more
 * values than there are fields, then the extraneous values are ignored.
 */
fun Iterable<List<String>>.asFields(fieldNames: List<String>): Sequence<Map<String, String>> =
    asSequence().asFields(fieldNames)

/**
 * Converts the sequence of rows to a [Sequence] of [Map]s of field names to values. If a row does not have as
 * many values as there are fields, its [Map] will not contain an entry for that field. If a value row has more
 * values than there are fields, then the extraneous values are ignored.
 */
fun Sequence<List<String>>.asFields(vararg fieldNames: String): Sequence<Map<String, String>> =
        asFields(fieldNames.toList())

/**
 * Converts the iterable of rows to a [Sequence] of [Map]s of field names to values. If a row does not have as
 * many values as there are fields, its [Map] will not contain an entry for that field. If a value row has more
 * values than there are fields, then the extraneous values are ignored.
 */
fun Iterable<List<String>>.asFields(vararg fieldNames: String): Sequence<Map<String, String>> =
    asSequence().asFields(*fieldNames)

/**
 * Reads all rows from a [CsvReader] as a [Sequence]. This method should not be invoked more than once
 * on any given [CsvReader] instance. The [CsvReader] must remain open until all rows have been read from the
 * returned [Sequence].
 */
fun CsvReader.readRows(): Sequence<List<String>> = generateSequence {
    readRow()?.toList()
}

/**
 * Writes all rows from a [Sequence] to a [CsvWriter].
 */
fun CsvWriter.writeRows(rows: Sequence<Iterable<String>>) = rows.forEach(::writeRow)

/**
 * Writes all rows from an [Iterable] to a [CsvWriter].
 */
fun CsvWriter.writeRows(rows: Iterable<Iterable<String>>) = rows.forEach(::writeRow)
