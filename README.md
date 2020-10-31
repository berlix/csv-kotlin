# A clean, fast, and convenient CSV library for Kotlin/JVM

The motivation for creating this library is to have a CSV reader/writer for Kotlin/JVM which is
* clean,
* lean,
* well-tested,
* fast,
* extensible,
* convenient.

The library exposes a straightforward interface that relies on standard Kotlin idioms. It has no dependencies
other than the Kotlin standard library.

## Quick start

    import pro.felixo.csv.*

### Reading CSV

For most use cases, use `readCsv` to obtain a sequence of parsed CSV rows:

    val rows: Sequence<List<String>> = File("data.csv").readCsv()

    rows.forEach { row: List<String> ->
        println(row)
    }

The `readCsv` convenience method can be used on `File`, `Path`, `Reader`, `InputStream`, and `String`.
In the following examples, `source` may be of any of those types.

You can specify the expected flavour of the CSV format and, for binary data sources, the character encoding:

    source
        .readCsv(
            format = CsvFormat(
                delimiter = '|',
                quote = '"',
                escape = '\\',
                terminator = "\r\n"
            ),
            charset = Charsets.UTF_32
        )

Use `withoutEmptyRows()` to skip empty lines in the input:

    val nonEmptyRows: Sequence<List<String>> =
        source
            .readCsv()
            .withoutEmptyRows()

Use `asFields()` on a row sequence to turn each row into a map of keys to values.
The first row of the input will determine the keys for all subsequent rows:

    val rows: Sequence<Map<String, String>> =
        source
            .readCsv()
            .asFields()

If the input contains no header row indicating the field names, you can pass them to `asFields()`:

    val rows: Sequence<Map<String, String>> =
        source
            .readCsv()
            .asFields("firstName", "lastName")

### Writing CSV

For most use cases, use `writeCsv` to write a `Sequence` or an `Iterable` of rows onto a `File`, `Path`, `Writer`, or
`OutputStream`:

    val rows = listOf(
        listOf("firstName", "lastName"),
        listOf("Chew", "Bacca")
    )

    File("data.csv").writeCsv(rows)

You can specify the desired flavour of the CSV format and, for binary destinations, the character encoding:

    destination
        .writeCsv(
            rows,
            format = CsvFormat(
                delimiter = '|',
                quote = '"',
                escape = '\\',
                terminator = "\r\n"
            ),
            charset = Charsets.UTF_32
        )

In order to convert a `Sequence` or an `Iterable` of `Map`s to a sequence of rows preceded by a header row,
use `asRowsWithHeaders`:

    val people = listOf(
        mapOf(
            "firstName" to "Chew",
            "lastName" to "Bacca"
        )
    )

    val rows = people.asRowsWithHeaders("firstName", "lastName")

    destination.writeCsv(rows)

## Advanced use cases

The examples in _Quick start_ use provided convenience methods only. If you need more control, use the classes
`CsvReader` and `CsvWriter` directly.
