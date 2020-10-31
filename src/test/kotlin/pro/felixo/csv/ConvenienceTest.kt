package pro.felixo.csv

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.StringWriter

private const val CSV = """h1,h2
v1,v2
v3,v4
"""

private val VALUE_ROWS = listOf(
    listOf("v1", "v2"),
    listOf("v3", "v4")
)

private val ROWS = listOf(listOf("h1", "h2")) + VALUE_ROWS

private val ROWS_AS_FIELDS = listOf(
    mapOf("h1" to "v1", "h2" to "v2"),
    mapOf("h1" to "v3", "h2" to "v4")
)

class ConvenienceTest {
    @Test
    fun `readCsv reads CSV from String`() =
        assertThat(CSV.readCsv().toList()).isEqualTo(ROWS)

    @Test
    fun `readCsv reads CSV from Reader`() =
        assertThat(CSV.reader().readCsv().toList()).isEqualTo(ROWS)

    @Test
    fun `readCsv reads CSV from File`() =
        assertThat(givenFile(CSV).readCsv().toList()).isEqualTo(ROWS)

    @Test
    fun `readCsv reads CSV from Path`() =
        assertThat(givenFile(CSV).toPath().readCsv().toList()).isEqualTo(ROWS)

    @Test
    fun `readCsv reads CSV from InputStream`() =
        assertThat(CSV.byteInputStream().readCsv().toList()).isEqualTo(ROWS)

    @Test
    fun `writeCsv writes CSV to Writer`() {
        val writer = StringWriter()
        writer.writeCsv(ROWS)
        assertThat(writer.toString()).isEqualTo(CSV)
    }

    @Test
    fun `writeCsvAndClose writes CSV to OutputStream`() {
        val stream = ByteArrayOutputStream()
        stream.writeCsvAndClose(ROWS)
        assertThat(stream.toString(Charsets.UTF_8.name())).isEqualTo(CSV)
    }

    @Test
    fun `writeCsv writes CSV to OutputStream`() {
        val stream = ByteArrayOutputStream()
        stream.use { it.writeCsv(ROWS) }
        assertThat(stream.toString(Charsets.UTF_8.name())).isEqualTo(CSV)
    }

    @Test
    fun `writeCsv writes CSV to File`() {
        val file = givenFile()
        file.writeCsv(ROWS)
        assertThat(file.readText()).isEqualTo(CSV)
    }

    @Test
    fun `writeCsv writes CSV to Path`() {
        val file = givenFile()
        file.toPath().writeCsv(ROWS)
        assertThat(file.readText()).isEqualTo(CSV)
    }

    @Test
    fun `asRowsWithHeaders converts maps to rows`() =
        assertThat(ROWS_AS_FIELDS.asRowsWithHeaders().toList()).isEqualTo(ROWS)

    @Test
    fun `asRowsWithHeaders converts maps to rows with headers specified as vararg`() =
        assertThat(ROWS_AS_FIELDS.asRowsWithHeaders("h1", "h2").toList()).isEqualTo(ROWS)

    @Test
    fun `asRowsWithHeaders converts maps to rows with headers specified as list`() =
        assertThat(ROWS_AS_FIELDS.asRowsWithHeaders(listOf("h1", "h2")).toList()).isEqualTo(ROWS)

    @Test
    fun `withoutEmptyRows filters out rows that contain only empty values`() =
        assertThat(
            listOf(
                listOf(),
                listOf("h1", "h2"),
                listOf(""),
                listOf("v1", "v2"),
                listOf("", ""),
                listOf("v3", "v4"),
                listOf(" ")
            ).withoutEmptyRows().toList()
        ).isEqualTo(
            listOf(
                listOf("h1", "h2"),
                listOf("v1", "v2"),
                listOf("v3", "v4"),
                listOf(" ")
            )
        )

    @Test
    fun `asFields uses first row as field names`() =
        assertThat(ROWS.asFields().toList()).isEqualTo(ROWS_AS_FIELDS)

    @Test
    fun `asFields uses field names specified as vararg`() =
        assertThat(VALUE_ROWS.asFields("h1", "h2").toList()).isEqualTo(ROWS_AS_FIELDS)

    @Test
    fun `asFields uses field names specified as list`() =
        assertThat(VALUE_ROWS.asFields(listOf("h1", "h2")).toList()).isEqualTo(ROWS_AS_FIELDS)

    @Test
    fun `readRows reads all rows`() =
        assertThat(CsvReader(CSV.reader()).readRows().toList()).isEqualTo(ROWS)

    @Test
    fun `writeRows writes all rows from Iterable`() {
        val writer = StringWriter()
        CsvWriter(writer).writeRows(ROWS)
        assertThat(writer.toString()).isEqualTo(CSV)
    }

    @Test
    fun `writeRows writes all rows from Sequence`() {
        val writer = StringWriter()
        CsvWriter(writer).writeRows(ROWS.asSequence())
        assertThat(writer.toString()).isEqualTo(CSV)
    }

    private fun givenFile(content: String = "") =
        File.createTempFile("test", ".csv").also {
            it.writeText(content)
            it.deleteOnExit()
        }
}
