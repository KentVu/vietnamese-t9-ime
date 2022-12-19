package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.DefaultApp
import com.github.kentvu.t9vietnamese.model.*
import com.google.common.truth.Truth
import okio.FileSystem
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue
import kotlin.test.asserter

class JvmTypingTest() {

    @Test
    fun typingTest() {
        val app: App = DefaultApp(
            KeyPad(listOf(
                KeysCollection.key2,
                KeysCollection.key3,
                KeysCollection.key4,
            )),
            VietnameseWordList,
            FileSystem.SYSTEM
        )
        app.init()
        app.type('2')
        assertContains(app.candidates, "2")
        app.type('4')
        assertContains(app.candidates, "24")
        app.type('2')
        Truth.assertThat(app.candidates).containsAtLeast(
            "cha", "chà", "chào"
        )
    }

}