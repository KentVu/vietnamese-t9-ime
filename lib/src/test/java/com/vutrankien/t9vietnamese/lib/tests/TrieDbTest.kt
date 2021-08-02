package com.vutrankien.t9vietnamese.lib.tests

import com.vutrankien.t9vietnamese.lib.Env
import com.vutrankien.t9vietnamese.lib.JavaLogFactory
import com.vutrankien.t9vietnamese.lib.TrieDb
import io.kotest.core.spec.style.FunSpec
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class TrieDbTest: FunSpec() {
    private val env: Env = mockk()
    private val trieDb = TrieDb(JavaLogFactory, env, DAWG_FILE)

    init {
        test("Load if dawg file already created") {
            every { env.fileExists(DAWG_FILE) } returns true
            trieDb.initOrLoad(emptySequence()) {}
            verify { trieDb.load() }
        }
        test("init if no dawg file exists") {
            every { env.fileExists(DAWG_FILE) } returns false
            trieDb.initOrLoad(emptySequence()) {}
            coVerify { trieDb.init(any(), any()) }
        }
    }

    companion object {
        private const val DAWG_FILE: String = "TestTrieDb.dawg"
    }
}