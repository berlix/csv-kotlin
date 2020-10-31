package pro.felixo.csv

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isNull
import assertk.assertions.matchesPredicate
import org.junit.jupiter.api.Test

class CsvReaderTest {
    @Test
    fun `reads no rows from empty input`() = assertThat(CsvReader("".reader(), CsvFormat()).readRow()).isNull()

    @Test
    fun `reads one row with one empty quoted value`() = assertRows("\"\"", listOf(""))

    @Test
    fun `reads one row with two empty unquoted values`() = assertRows(",", listOf("",""))

    @Test
    fun `reads an empty line as a row with one empty value`() =
        assertRows("value1\n\nvalue2", listOf("value1"), listOf(""), listOf("value2"))

    @Test
    fun `reads one row with one value`() = assertRows("value", listOf("value"))

    @Test
    fun `ignores trailing linebreak`() = assertRows("value\n", listOf("value"))

    @Test
    fun `ignores trailing linebreak after quoted value`() = assertRows("\"value\"\n", listOf("value"))

    @Test
    fun `reads empty lines after unquoted value`() =
        assertRows("value1\n\n\nvalue2", listOf("value1"), listOf(""), listOf(""), listOf("value2"))

    @Test
    fun `reads empty lines after quoted value`() =
        assertRows("\"value1\"\n\n\nvalue2", listOf("value1"), listOf(""), listOf(""), listOf("value2"))

    @Test
    fun `reads two rows with two values each`() =
        assertRows("value1,value2\nvalue3,value4", listOf("value1", "value2"), listOf("value3", "value4"))

    @Test
    fun `reads UNIX linebreak`() =
        assertRows("value1\nvalue2", listOf("value1"), listOf("value2"))

    @Test
    fun `reads Windows linebreak`() =
        assertRows("value1\r\nvalue2", listOf("value1"), listOf("value2"))

    @Test
    fun `reads Mac linebreak`() =
        assertRows("value1\rvalue2", listOf("value1"), listOf("value2"))

    @Test
    fun `reads quoted value`() = assertRows("\"value\"", listOf("value"))

    @Test
    fun `reads quoted value containing escaped quote`() = assertRows("\"\"\"va\"\"lue\"\"\"", listOf("\"va\"lue\""))

    @Test
    fun `reads quoted value containing delimiter`() = assertRows("\",va,lue,\"", listOf(",va,lue,"))

    @Test
    fun `reads quoted value containing linebreaks`() = assertRows("\"\rva\nlue\r\n\"", listOf("\rva\nlue\r\n"))

    @Test
    fun `throws if quoted value terminated by EOF`() = assertThrows("\"value", 0)

    @Test
    fun `throws if quoted value is not followed by delimiter`() =
        assertThrows("\"value1\" ,value2", 0)

    @Test
    fun `throws on parsing error in non-zero row number`() =
        assertThrows("value1\n\n\"value2", 2)

    @Test
    fun `row number in exceptions does not count linebreaks in values`() =
        assertThrows("\"val\nue1\"\n\n\"value2", 2)

    private fun assertRows(input: String, vararg expected: List<String>) {
        CsvReader(input.reader(), CsvFormat()).use {
            assertThat(it.readRows().toList()).isEqualTo(expected.toList())
        }
    }

    private fun assertThrows(input: String, expectedRowNumber: Int) {
        CsvReader(input.reader(), CsvFormat()).use {
            assertThat { it.readRows().toList() }.isFailure()
                .transform { it as CsvException }.matchesPredicate { it.rowNumber == expectedRowNumber }
        }
    }
}
