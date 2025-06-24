package com.github.kentvu.t9vietnamese

import doist.x.normalize.Form
import doist.x.normalize.normalize
import kotlin.test.Test
import kotlin.test.assertEquals

class DecomposeVietnameseTest {
    @Test
    fun decomposeVietnameseTest() {
        val s = "đ"
        val decomposed = s.normalize(Form.NFKD)
        println(decomposed)
        assertEquals(s, decomposed)
    }
}