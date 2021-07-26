package com.vutrankien.t9vietnamese.lib

import kentvu.dawgjava.DawgTrie
import kentvu.dawgjava.Trie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class TrieDb(
        lg: LogFactory,
        private val env: Env,
        dawgFile: String = "T9Engine.dawg"
) : Db {
    companion object {
        private const val DEFAULT_REPORT_PROGRESS_INTERVAL = 10
    }

    private val dawgPath = "${env.workingDir}/$dawgFile"
    private val log = lg.newLog("T9Engine")
    private lateinit var trie: Trie
    override var initialized: Boolean = false
        private set

    override fun canReuse(): Boolean {
        env.fileExists(dawgPath).let {
            log.d("canReuseDb: $it")
            return it
        }
    }

    override fun load() {
        log.d("initFromDb")
        trie = DawgTrie.load(dawgPath)
        initialized = true
    }

    override fun search(prefix: String): Map<String, Int> = trie.search(prefix)

    override suspend fun init(
            seed: Sequence<String>,
            onBytes: suspend (Int) -> Unit
    ) = coroutineScope {
        log.d("init: fromSeed,initialized=$initialized")
        val channel = Channel<Int>()
        launch(Dispatchers.IO) {
            trie = DawgTrie.build(dawgPath, seed, channel)
        }
        var markPos = 0
        val count = 0
        for (bytes in channel) {
            if (count - markPos == DEFAULT_REPORT_PROGRESS_INTERVAL) {
                onBytes.invoke(bytes)
                //print("progress $count/${sortedWords.size}: ${bytes.toFloat() / size * 100}%\r")
                markPos = count
            }
        }
        initialized = true
    }
}