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
    dawgFile: String = "T9Engine.dawg",
    /** set `true` to force recreate dawgFile. */
    private val overwriteDawgFile: Boolean = false
) : Db {
    companion object {
        private const val DEFAULT_REPORT_PROGRESS_INTERVAL = 10
    }

    private val dawgPath = "${env.workingDir}/$dawgFile"
    private val log = lg.newLog("TrieDb")
    private lateinit var trie: Trie
    override var initialized: Boolean = false
        private set

    private fun canReuse(): Boolean {
        env.fileExists(dawgPath).let {
            log.d("canReuseDb: $it")
            return it
        }
    }

    /**
     * Init from built db.
     * Visible for test only.
     */
    fun load() {
        log.d("load")
        trie = DawgTrie.load(dawgPath)
    }

    override fun search(prefix: String): Map<String, Int> = trie.search(prefix)/*.also {
        log.v("search:$prefix:return $it")
    }*/

    override suspend fun initOrLoad(
            seed: Seed,
            onBytes: suspend (Int) -> Unit
    ) {
        if (overwriteDawgFile || !canReuse()) {
            log.i("init: initialized=$initialized from seed")
            init(seed, onBytes)
        } else {
            log.d("init: initialized=$initialized,canReuse -> load")
            load()
        }
        initialized = true
    }

    suspend fun init(
        seed: Seed,
        onBytes: suspend (Int) -> Unit
    ) {
        coroutineScope {
            log.d("load")
            val channel = Channel<Int>()
            launch (Dispatchers.IO) {
                trie = DawgTrie.build(dawgPath, seed.sequence(), channel)
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
        }
    }
}