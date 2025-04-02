package com.github.kentvu.t9vietnamese

import doist.x.normalize.Form
import doist.x.normalize.normalize
import kotlin.test.Test
import kotlin.test.assertEquals

class DecomposeVietnameseTest {
    @Test
    fun decomposeVietnameseTest() {
        val s = "đ"
        val normalized = s.normalize(Form.NFKD)
        println(normalized)
        assertEquals(s, normalized)
    }
}