package pro.felixo.csv

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.io.StringWriter

class CsvWriterTest {
    @Test
    fun `writes empty row`() = assertRows("\n", emptyList())

    @Test
    fun `writes row with empty value`() = assertRows("\n", listOf(""))

    @Test
    fun `writes row with one simple value`() = assertRows("value\n", listOf("value"))

    @Test
    fun `quotes if value contains quote char`() = assertRows("\"a\\\"b\"\n", listOf("a\"b"))

    @Test
    fun `does not quote if value contains escape char`() = assertRows("a\\b\n", listOf("a\\b"))

    @Test
    fun `quotes if value contains delimiter`() = assertRows("\"a,b\"\n", listOf("a,b"))

    @Test
    fun `quotes if value contains linebreak`() = assertRows("\"a\nb\"\n", listOf("a\nb"))

    @Test
    fun `writes two rows with two values each`() =
        assertRows("value1,value2\nvalue3,value4\n", listOf("value1", "value2"), listOf("value3", "value4"))

    private fun assertRows(expected: String, vararg input: List<String>) {
        val out = StringWriter()
        CsvWriter(out, CsvFormat(escape = '\\')).use { it.writeRows(input.asSequence()) }
        assertThat(out.toString()).isEqualTo(expected)
    }
}
