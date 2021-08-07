package com.vutrankien.t9vietnamese.lib.tests

import com.vutrankien.t9vietnamese.lib.*
import io.kotest.core.spec.style.FunSpec
import io.mockk.*

class TrieDbTest: FunSpec() {

    private val env: Env = mockk {
        every { workingDir } returns JvmEnv.workingDir
    }

    private val trieDb = spyk(TrieDb(JavaLogFactory, env, DAWG_FILE))

    init {
        test("Load if dawg file already created") {
            every { env.fileExists(match {it.contains(DAWG_FILE)}) } returns true
            trieDb.initOrLoad(Seed.EmptySeed) {}
            verify { trieDb.load() }
        }
        test("init if no dawg file exists") {
            every { env.fileExists(match {it.contains(DAWG_FILE)}) } returns false
            trieDb.initOrLoad(Seed.EmptySeed) {}
            coVerify { trieDb.init(any(), any()) }
        }
    }

    companion object {
        private const val DAWG_FILE: String = "TestTrieDb.dawg"
    }
}