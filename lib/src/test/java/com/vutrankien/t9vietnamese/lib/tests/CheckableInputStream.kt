package com.vutrankien.t9vietnamese.lib.tests

import java.io.InputStream

/**
 * assert inputStream has closed?
 */
class CheckableInputStream(val delegated: InputStream) : InputStream() {
    var closed = false
        private set

    override fun read(): Int =
        delegated.read()

    override fun close() {
        delegated.close()
        super.close()
        closed = true
    }
}