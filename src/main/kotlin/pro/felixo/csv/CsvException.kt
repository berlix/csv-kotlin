package pro.felixo.csv

/**
 * An exception that is thrown upon malformed CSV input data.
 *
 * @param rowNumber The zero-based index of the row that could not be parsed. Note that, in case the input contains
 *                  values that contain linebreaks, this number may not be the same as the line number of the CSV data
 *                  when viewed as plain text.
 */
class CsvException(message: String, val rowNumber: Int): Exception("$message in row $rowNumber")
