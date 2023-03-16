package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.model.*

class Engine(private val ui: UI, private val trie: Trie) {
    var candidates: CandidateSelection = CandidateSelection()
        private set
    private val fullSequence = StringBuilder(10)
    private var prefixes: Set<String> = emptySet()

    fun type(keySequence: String) {
        keySequence.forEach { k ->
            type(VNKeys.fromChar(k))
        }
    }
    fun type(keySequence: List<Key>) {
        keySequence.forEach { key ->
            //if(key.isWordTerminal)
            type(key)
        }
    }

    fun type(key: Key) {
        if (key == VNKeys.Clear) {
            reset()
            ui.update(UI.UpdateEvent.UpdateCandidates(candidates))
            return
        }
        if (key == VNKeys.keyStar) {
            //ui.update(UI.UpdateEvent.SelectNextCandidate)
            candidates=candidates.advanceSelectedCandidate()
            ui.update(UI.UpdateEvent.UpdateCandidates(candidates))
            return
        }
        if (key == VNKeys.key0) {
            ui.update(UI.UpdateEvent.Confirm(candidates.selectedCandidate))
            reset()
            return
        }

        fullSequence.append(key.symbol)
        val _candidates = linkedSetOf(fullSequence.toString())
        val _prefixes = linkedSetOf<String>()
        if (fullSequence.length == 1) {
            // Only start searching from 2nd key to prevent too many candidates
            //_candidates.addAll(key.subChars.map { "$it" })
            //prefixes = it.toSet() // assuming trie has 1 character prefix
            key.subChars.map { "$it" }.forEach { c ->
                if (trie.containsPrefix(c)) {
                    _candidates.add(c)
                    _prefixes.add(c)
                }
            }
        } else {
            prefixes.forEach { pf ->
                key.subChars.forEach { sc ->
                    if (trie.containsPrefix(pf + sc)) {
                        _prefixes.add(pf + sc)
                        _candidates.addAll(trie.prefixSearch(pf + sc))
                    }
                }
            }
        }
        prefixes = _prefixes
        candidates = CandidateSelection.from(_candidates)
        ui.update(UI.UpdateEvent.UpdateCandidates(candidates))
    }

    private fun reset() {
        prefixes = emptySet()
        candidates = CandidateSelection()
        fullSequence.clear()
        //selectedCandidateId=0
    }

}