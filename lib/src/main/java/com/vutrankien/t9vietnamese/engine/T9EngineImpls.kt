package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.*
import kentvu.dawgjava.DawgTrie
import kentvu.dawgjava.Trie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.Normalizer


internal fun String.composeVietnamese() = Normalizer.normalize(
    this,
    Normalizer.Form.NFKC
)

class DefaultT9Engine(
        lg: LogFactory,
        private val env: Env,
        dawgFile: String = "T9Engine.dawg",
        override val pad: PadConfiguration
) : T9Engine {
    private val dawgPath = "${env.workingDir}/$dawgFile"
    private val log = lg.newLog("T9Engine")
    private lateinit var trie: Trie
    override var initialized: Boolean = false
        private set

    override val eventSource: Channel<T9Engine.Event> = Channel()

    private val _currentCandidates = mutableSetOf<String>()
    private val _currentNumSeq = mutableListOf<Key>()
    private val findCandidates by lazy {
        FindCandidates(trie, pad, 10, log)
    }
    // Preselect 1st candidate
    private var _selectedCandidate = 0

    override suspend fun init(
        seed: Sequence<String>
    ) = coroutineScope {
        log.d("init: fromSeed")
        val channel = Channel<Int>()
        launch(Dispatchers.IO) {
            trie = DawgTrie.build(dawgPath, seed, channel)
        }
        var markPos = 0
        val count = 0
        for (bytes in channel) {
            if (count - markPos == REPORT_PROGRESS_INTERVAL) {
                eventSource.send(T9Engine.Event.LoadProgress(bytes))
                //print("progress $count/${sortedWords.size}: ${bytes.toFloat() / size * 100}%\r")
                markPos = count
            }
        }
        initialized = true
        eventSource.send(T9Engine.Event.Initialized)
    }

    override fun canReuseDb(): Boolean {
        env.fileExists(dawgPath).let {
            log.d("canReuseDb: $it")
            return it
        }
    }

    override fun initFromDb() {
        log.d("initFromDb")
        trie = DawgTrie.load(dawgPath)
    }

    override suspend fun push(key: Key) {
        _currentNumSeq.add(key)
        when (pad[key].type) {
            KeyType.NextCandidate -> {
                _selectedCandidate++
                eventSource.send(T9Engine.Event.SelectCandidate(_selectedCandidate))
            }
            KeyType.PrevCandidate -> {
                _selectedCandidate--
                eventSource.send(T9Engine.Event.SelectCandidate(_selectedCandidate))
            }
            KeyType.Confirm -> {
                // TODO implement selecting feature
                val selected = if (_currentCandidates.isEmpty()) {
                    // Return the number sequence if no match found.
                    _currentNumSeq
                        .dropLast(1) // Drop the "confirm" key
                        .joinNum()
                } else {
                    _currentCandidates.elementAt(_selectedCandidate)
                }
                eventSource.send(T9Engine.Event.Confirm(selected))
                _currentCandidates.clear()
                _currentNumSeq.clear()
                _selectedCandidate = 0
            }
            else -> {
                // Numeric keys
                val numSeqStr = _currentNumSeq.joinNum()
                log.d("push:$numSeqStr")
                val candidates = findCandidates(_currentNumSeq).toMutableList()
                candidates.add(numSeqStr)
                eventSource.send(T9Engine.Event.NewCandidates(candidates))
                _currentCandidates.clear()
                _currentCandidates.addAll(candidates)
                // XXX: don't use _currentCandidates directly because it is being used on multiple threads
            }
        }
    }

    companion object {
        private const val REPORT_PROGRESS_INTERVAL = 10

        private fun Iterable<Key>.joinNum(): String = buildString {
            this@joinNum.forEach {
                append(it.char)
            }
        }

        /**
         * Support case-insensitive matching.
         */
        @OptIn(ExperimentalStdlibApi::class)
        private fun Set<Char>.fillCase(): Set<Char> = buildSet {
            this@fillCase.forEach {
                add(it)
                if (Character.isUpperCase(it))
                    add(it.toLowerCase())
                else if (Character.isLowerCase(it))
                    add (it.toUpperCase())
            }
        }
    }

    class FindCandidates(
        private val trie: Trie,
        private val pad: PadConfiguration,
        private val limit: Int/*TODO*/,
        private val log: LogFactory.Log
    ) {
        operator fun invoke(
            keySeq: List<Key>
        ): Set<String> {
            // find all combinations:
            val possibleCombinations = possibleCombinations("", keySeq)
            return possibleCombinations.fold(sortedSetOf<String>()) {acc, next ->
                acc.apply { addAll(trie.search(next).keys.map { it.composeVietnamese() }) }
            }
        }

        private fun possibleCombinations(currentPrefix: String, keySeq: List<Key>): Set<String> {
            val possibleChars = linkedSetOf<Char>()
            pad[keySeq[0]].chars.fillCase().forEach {c ->
                val newCombination = "$currentPrefix$c"
                val searchResult = trie.search(newCombination).keys
                if (searchResult.isNotEmpty())
                    possibleChars.add(c)
            }
            if (keySeq.size == 1) {
                return possibleChars.map {
                    "$currentPrefix$it"
                }.toSet().also {
                    log.v("possibleCombinations:$it")
                }
            }
            // else
            val result = LinkedHashSet<String>()
            possibleChars.forEach { c ->
                result.addAll(possibleCombinations(currentPrefix + c, keySeq.drop(1)))
            }
            return result
        }
    }

}

