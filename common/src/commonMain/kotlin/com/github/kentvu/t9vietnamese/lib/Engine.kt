package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.Presenter
import com.github.kentvu.t9vietnamese.model.*
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.KeyPads
import kotlin.Exception
import kotlin.apply
import kotlin.text.deleteAt
import kotlin.text.lastIndex
import kotlin.text.map

class Engine(
    private val ui: Presenter,
    private val trie: Trie,
) {
    private var mode = EditorInfo(EditorInfo.Class.Normal)
    private lateinit var keyPad: KeyPad
    private val inputConnection: InputSystemConnection = ui.inputConnection
    private var candidates: CandidateSelection = CandidateSelection()
    private val fullSequence = StringBuilder(10)
    private var prefixes: Set<String> = emptySet()
    private val prefixesCache= mutableMapOf<String, Set<String>>()
    private var shiftMode: Boolean = false

    /*fun type(keySequence: String) {
        keySequence.forEach { k ->
            type(VNKeys.fromChar(k))
        }
    }*/
    fun type(keySequence: List<Action>) {
        keySequence.forEach { key ->
            //if(key.isWordTerminal)
            type(key)
        }
    }

    fun type(action: Action) {
        if (mode.aClass == EditorInfo.Class.Normal)
            typeNormal(action)
        if (mode.aClass == EditorInfo.Class.Number)
            typeNumber(action)
    }

    private fun typeNumber(action: Action) {
        if (action == Action.Ok) {
            if (isComposing()) {
                inputConnection.commitText(candidates.selectedCandidate.text)
                reset()
                ui.updateState { it.copy(candidates = candidates) }
            } else inputConnection.performEditorAction()
            return
        }
        if (action == Action.Return) {
            if (isComposing()) {
                inputConnection.commitText(candidates.selectedCandidate.text)
                reset()
                ui.updateState { it.copy(candidates = candidates) }
            } else inputConnection.commitText("${Action.Return.rawChar}")
            return
        }
        if (action == Action.Star) {
            if (isComposing()) {
                candidates = candidates.advanceSelectedCandidate()
                ui.updateState { it.copy(candidates = candidates) }
                return
            }
        }
        if (action == Action.Hash) {
            // Hash button: Go to last candidate.
            candidates = candidates.select(candidates.lastIndex())
            ui.updateState { it.copy(candidates = candidates) }
            return
        }

        if (action == Action.Backspace) {
            inputConnection.deleteSurroundingText(1, 0)
            return
        }

        if(isComposing()) {
            inputConnection.commitText(candidates.selectedCandidate.text)
            reset()
            ui.updateState { it.copy(candidates = candidates) }
        }

        val subChars = try {
            keyPad.numericSubstitution(action)
        } catch (e: Exception) {
            null
        }
        if (subChars != null) {
            candidates = CandidateSelection.from(buildList {
                addAll(subChars.map { "$it" })
                add("${action.rawChar}")
            })
            log.debug("updateState:$candidates")
            ui.updateState { it.copy(candidates = candidates) }
        } else {
            inputConnection.commitText("${action.rawChar}")
            reset()
            ui.updateState { it.copy(candidates = candidates) }
        }
    }

    fun typeNormal(action: Action) {
        if (action == Action.Clear) {
            reset()
            ui.updateState { it.copy(candidates = candidates) }
            return
        }
        if (action == Action.Star) {
            //ui.update(UI.UpdateEvent.SelectNextCandidate)
            candidates = candidates.advanceSelectedCandidate()
            ui.updateState { it.copy(candidates = candidates) }
            return
        }
        if (action == Action.Hash) {
            // Commit fullSequence directly.
            /*ui.inputConnection.commitText(fullSequence.toString())
            reset()
            ui.update(UI.UpdateEvent.UpdateCandidates(candidates))*/
            // Hash button: select the number sequence.
            candidates = candidates.select(candidates.lastIndex())
            ui.updateState { it.copy(candidates = candidates) }
            return
        }
        if (action == Action.Space) {
            if (isComposing()) {
                // commit current composing word with a space.
                inputConnection.commitText(candidates.selectedCandidate.text + " ")
            } else {
                inputConnection.commitText("${Action.Space.rawChar}")
            }
            reset()
            ui.updateState { it.copy(candidates = candidates) }
            return
        }
        if (action == Action.Ok) {
            if (isComposing()) {
                inputConnection.commitText(candidates.selectedCandidate.text)
            } else {
                inputConnection.performEditorAction()
            }
            reset()
            ui.updateState { it.copy(candidates = candidates) }
            return
        }
        if (action == Action.Return) {
            if (isComposing()) {
                // if we're in middle of composing a word, commit without a space.
                inputConnection.commitText(candidates.selectedCandidate.text)
            } else {
                inputConnection.commitText("\n")
            }
            reset()
            ui.updateState { it.copy(candidates = candidates) }
            return
        }
        if (action == Action.Zero) {
            fullSequence.append(Action.Zero.rawChar)
            prefixes = emptySet()
            updateCandidateSelection(emptySet())
            return
        }
        if (action == Action.One) {
            if (isComposing()) {
                inputConnection.commitText(candidates.selectedCandidate.text)
                reset()
                ui.updateState { it.copy(candidates = candidates) }
            } else {
                fullSequence.append(Action.One.rawChar)
                updateCandidateSelection(getPunctuationMarks())
            }
            return
        }
        if (action == Action.Shift) {
            shiftMode = !shiftMode
            if (isComposing()) {
                updateCandidateSelection(
                    sortByLength(expandPrefixes(prefixes)).toSet(),
                    preserveSel = true,
                )
            }
            return
        }

        if (action == Action.Backspace) {
            if (isComposing()) {
                fullSequence.apply { deleteAt(lastIndex) }
                prefixes = prefixesCache[fullSequence.toString()] ?: emptySet()
                updateCandidateSelection(
                    if (prefixes.isNotEmpty())
                        sortByLength(expandPrefixes(prefixes)).toSet()
                    else if (fullSequence.toString().lastOrNull() == Action.One.rawChar)
                        getPunctuationMarks()
                    else emptySet()
                )
            } else {
                inputConnection.deleteSurroundingText(1, 0)
            }
            return
        }

        fullSequence.append(action.rawChar)
        val _prefixes = linkedSetOf<String>()
        val subChars = keyPad.numericSubstitution(action)
        if (fullSequence.length == 1) {
            // Only start searching from 2nd key to prevent too many candidates
            val _candidates = linkedSetOf<String>()
            subChars.map { "$it" }.forEach { c ->
                if (trie.containsPrefix(c)) {
                    _candidates.add(c)
                    _prefixes.add(c)
                }
            }
            prefixes = _prefixes
            prefixesCache[fullSequence.toString()] = _prefixes
            updateCandidateSelection(_candidates)
            return
        } else {
            prefixes.forEach { pf ->
                subChars.forEach { sc ->
                    if (trie.containsPrefix(pf + sc)) {
                        _prefixes.add(pf + sc)
                    }
                }
            }
            prefixes = _prefixes
            prefixesCache[fullSequence.toString()] = _prefixes
        }
        updateCandidateSelection(
            sortByLength(
                expandPrefixes(prefixes)
            ).toSet()
        )
    }

    private fun getPunctuationMarks(): Set<String> = buildSet {
        addAll(
            keyPad.punctualMarksKey.subChars
                ?.map { "$it" } ?: error("Punctual marks key should define punctual marks!!"))
        //add("${Action.One.rawChar}")
    }

    private fun expandPrefixes(prefixes: Set<String>): Set<String> {
        return buildSet {
            prefixes.forEach { pf ->
                if (trie.containsPrefix(pf))
                    addAll(trie.prefixSearch(pf))
            }
        }
    }

    private fun expandPrefixesTo(_candidates: LinkedHashSet<String>) {
        _candidates.addAll(
            expandPrefixes(prefixes)
        )
    }

    private fun sortByLength(cands: Set<String>) = cands
        .groupBy { it.length }
        .values.flatten().run {
            if (!shiftMode) this
            else map { it.replaceFirstChar(Char::uppercaseChar) }
        }

    private fun updateCandidateSelection(cands: Set<String>, preserveSel: Boolean = false) {
        candidates = CandidateSelection.from(
            buildList {
                addAll(cands)
                // Put number sequence as the last candidate.
                if (fullSequence.isNotEmpty())
                    add(fullSequence.toString())
            },
            if (preserveSel) candidates.selectedCandidateId else 0
        )
        ui.updateState { it.copy(candidates = candidates) }
    }

    private fun isComposing() = candidates.isNotEmpty()

    private fun reset() {
        prefixes = emptySet()
        candidates = CandidateSelection()
        fullSequence.clear()
        shiftMode = false
    }

    fun selectCandidate(candidateId: Int) {
        candidates = candidates.select(candidateId)
        ui.updateState { it.copy(candidates = candidates) }
    }

    fun switchMode(editorInfo: EditorInfo) {
        log.debug("Switching mode: $editorInfo")
        mode = editorInfo
        //keyPad = KeyPads.VN
        keyPad = when (mode.aClass) {
            EditorInfo.Class.Normal -> KeyPads.VN
            EditorInfo.Class.Number -> KeyPads.Numeric
        }
        ui.updateState { it.copy(
            keyPad = keyPad
        ) }
    }

    companion object {
        private val log = Logger.tag("Engine")
    }
}
