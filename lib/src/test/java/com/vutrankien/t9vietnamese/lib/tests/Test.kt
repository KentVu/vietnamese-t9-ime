package com.vutrankien.t9vietnamese.lib.tests

import io.kotest.core.spec.style.FunSpec

interface Test {
    val name: String

    suspend fun go()

}
