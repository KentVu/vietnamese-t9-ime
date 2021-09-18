package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.channels.Channel
import java.text.Normalizer


internal fun String.composeVietnamese() = Normalizer.normalize(
        this,
        Normalizer.Form.NFKC
)

class DefaultT9Engine(
    override val engineSeed: Seed,
    override val pad: PadConfiguration,
    lg: LogFactory,
    private val db: Db
) : T9Engine {
    private val log = lg.newLog("T9Engine")
    override val initialized: Boolean
        get() = db.initialized

    override val eventSource: Channel<T9Engine.Event> = Channel()

    private val _candidates = Candidates(log)

    class Candidates(
        val log: LogFactory.Log,
        private val candidates: MutableSet<String> = mutableSetOf()
    ) : Collection<String> by candidates {

        // Preselect 1st candidate
        internal var selectedCandidate = 0

        //fun empty(): Boolean = candidates.isEmpty()
        //fun elementAt(pos: Int): String = candidates.elementAt(pos)
        fun clear() {
            selectedCandidate = 0
            candidates.clear()
        }

        fun update(candidates: Collection<String>, numSeq: MutableList<Key>) {
            val numSeqStr = numSeq.joinNum()
            // XXX: don't update _currentCandidates directly because it is being used on multiple threads
            clear()
            this.candidates.addAll(candidates.toMutableList().apply { add(numSeqStr) })
            //log.d("Candidates:update:$candidates:$numSeqStr:out ${this.candidates}")
        }

        fun next() {
            selectedCandidate++
        }

        fun prev() {
            if (selectedCandidate > 0) {
                selectedCandidate--
            } else {
                log.v("Candidates:prev: Navigating back too much!")
            }
        }

        fun selected(): String = candidates.elementAt(selectedCandidate)

        override fun toString(): String {
            return "DefaultT9Engine.Candidates:$candidates"
        }
    }

    private val _currentNumSeq = mutableListOf<Key>()
    private val findCandidates by lazy {
        FindCandidates(db, pad, 10, log)
    }

    private val isComposing get() = _currentNumSeq.count() > 0

    override suspend fun push(key: Key) {
        when (pad[key].type) {
            KeyType.NextCandidate -> {
                _candidates.next()
                eventSource.send(T9Engine.Event.SelectCandidate(_candidates.selectedCandidate))
            }
            KeyType.PrevCandidate -> {
                _candidates.prev()
                eventSource.send(T9Engine.Event.SelectCandidate(_candidates.selectedCandidate))
            }
            KeyType.Confirm -> {
                if (!isComposing) {
                    // TODO send a space when not composing
                    return
                }
                // XXX Insert space based on situation.
                eventSource.send(T9Engine.Event.Confirm("${_candidates.selected()} "))
                _candidates.clear()
                _currentNumSeq.clear()
            }
            KeyType.Backspace -> {
                if (isComposing) {
                    _currentNumSeq.removeLast()
                    _candidates.update(findCandidates(_currentNumSeq), _currentNumSeq)
                    eventSource.send(T9Engine.Event.NewCandidates(_candidates))
                } else {
                    eventSource.send(T9Engine.Event.Backspace)
                }
            }
            else -> {
                // Numeric keys
                _currentNumSeq.add(key)
                _candidates.update(findCandidates(_currentNumSeq), _currentNumSeq)
                //log.v("push:$key:out $_candidates")
                // XXX clone _candidates or event get dropped (I don't know why :( )
                eventSource.send(T9Engine.Event.NewCandidates(_candidates.toList()))
            }
        }
    }

    override suspend fun init() {
        db.initOrLoad(engineSeed) { bytes ->
            eventSource.send(T9Engine.Event.LoadProgress(bytes))
        }
        eventSource.send(T9Engine.Event.Initialized)
    }

    companion object {
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
                    add(it.toUpperCase())
            }
        }
    }

    class FindCandidates(
            private val db: Db,
            private val pad: PadConfiguration,
            private val limit: Int/*TODO*/,
            private val log: LogFactory.Log
    ) {
        operator fun invoke(
                keySeq: List<Key>
        ): Set<String> {
            // find all combinations:
            val possibleCombinations = possibleCombinations("", keySeq)
            //log.v("FindCandidates:possibleCombinations:$possibleCombinations")
            return possibleCombinations.fold(sortedSetOf<String>()) { acc, next ->
                acc.apply {
                    addAll(db.search(next).keys.map { it.composeVietnamese() })
                    //log.v("FindCandidates:acc=$acc,next=${next},$this")
                }
            }/*.also { log.v("FindCandidates:return $it") }*/
        }

        private fun possibleCombinations(currentPrefix: String, keySeq: List<Key>): Set<String> {
            val possibleChars = linkedSetOf<Char>()
            pad[keySeq[0]].chars.fillCase().forEach { c ->
                val newCombination = "$currentPrefix$c"
                if (dbContainsPrefix(newCombination)) {
                    possibleChars.add(c)
                }
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

        /**
         * Poll db for prefix.
         */
        private fun dbContainsPrefix(combination: String): Boolean {
            val searchResult = db.search(combination).keys
            return searchResult.isNotEmpty()
        }
    }

}

