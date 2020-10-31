package pro.felixo.csv

/**
 * Describes the parameters of a CSV format.
 */
data class CsvFormat(
    /**
     * The character which delimits values in a row. Defaults to [DEFAULT_DELIMITER].
     */
    val delimiter: Char = DEFAULT_DELIMITER,

    /**
     * Values that contain the [delimiter], the [quote], or the [terminator], are enclosed in a pair of [quote]
     * characters. Defaults to [DEFAULT_QUOTE].
     */
    val quote: Char = DEFAULT_QUOTE,

    /**
     * Occurrences of the [quote] character within values which are quoted are prefixed with this character.
     * Defaults to [quote].
     */
    val escape: Char = quote,

    /**
     * The row termination string to use when writing. When reading, the common linebreak character sequences
     * ("\n", "\r", "\r\n") are recognised automatically, and this value is ignored. Defaults to [DEFAULT_TERMINATOR].
     */
    val terminator: String = DEFAULT_TERMINATOR
) {
    /**
     * If a value contains any of these characters, it should be quoted.
     */
    val specialChars by lazy { listOf(delimiter, quote) + terminator.toList() }

    companion object {
        const val DEFAULT_DELIMITER = ','
        const val DEFAULT_QUOTE = '"'
        const val DEFAULT_TERMINATOR = "\n"
    }
}
