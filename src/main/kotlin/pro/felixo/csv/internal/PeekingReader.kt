package pro.felixo.csv.internal

import java.io.Reader

class PeekingReader(private val reader: Reader): AutoCloseable {
    init { read() }

    var next: Char? = null
        private set

    fun read(): Char? {
        val ret = next
        next = reader.read().takeIf { it != -1 }?.toChar()
        return ret
    }

    override fun close() = reader.close()
}
