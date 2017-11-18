package com.vutrankien.t9vietnamese

import android.content.Context
import timber.log.Timber.d
import timber.log.Timber.w
import java.io.Closeable
import java.text.Normalizer

private val log = KLog("T9Engine")

val LOCALE_VN = "vi-VN"
val LOCALE_US = "en-US"

class T9Engine @Throws(EnginePromise::class)
constructor(context: Context, locale: String): Closeable {
    val configurations = Configuration(locale)
    val dbWrapper : DBWrapper = TrieDB(context.filesDir)

    private val currentNumSeq = mutableListOf<Char>()
    private var _currentCandidates = setOf<String>()
    var currentCandidates: Set<String>
        get() = if (!numOnlyMode)
            _currentCandidates.map { it.composeVietnamese() }.toSet()
        else setOf(currentNumSeq.joinToString(separator = ""))
        private set(value) {
            _currentCandidates = value
        }

    private var currentCombinations = setOf<String>()
    /**
     * * `true`: No more candidates from DB, returning current numseq
     */
    private var numOnlyMode = false

    init {
        if (dbWrapper.haveMagic()) {
            log.i("DB OK!")
        } else {
            throw EnginePromise(this, context)
        }
    }

    fun initialize(context: Context) {
        log.i("Destroying malicious database and reopen it!")
        dbWrapper.clear()
        val inputStream = context.assets.open(configurations.wordListFile)
        var step = 0
        var bytesRead = 0
        // https://stackoverflow.com/a/6992255
        val flength = inputStream.available()
        inputStream.bufferedReader().useLines {
            var count = 0
            it.groupBy {
                val numEachTransaction = 200
                count++
                return@groupBy count/numEachTransaction
            }.values.forEach {
                dbWrapper.putAll(
                        it.map {
                            it.decomposeVietnamese()
                        }
                )
                val lastStep = step
                bytesRead += it.fold(0) { i, s ->
                    i + s.toByteArray().size + 1
                }
                step = (bytesRead / (flength / 100))
                if (lastStep != step) d("Written $bytesRead / $flength to db ($step%)")
            }
        }
        // put magic at last to mark database stable (avoid app crash)
        dbWrapper.putMagic()
    }

    override fun close() {
        dbWrapper.close()
    }

    fun input(numseq: String) {
        numseq.forEach { input(it) }
    }

    private fun <E> MutableList<E>.push(num: E) = add(num)

    fun input(num: Char) {
        currentNumSeq.push(num)
        if (!numOnlyMode) {
            currentCombinations *= (configurations.pad[num].chars)
            // filter combinations
            if (currentNumSeq.size > 1) {
                // Only start from 2 numbers and beyond
                _currentCandidates = dbWrapper.existingPrefix(currentCombinations)
                if (!_currentCandidates.isEmpty())
                    currentCombinations = currentCombinations.filter { comb ->
                            _currentCandidates.any { it.startsWith(comb) }
                        }.toSet()
                else {
                    w("seq$currentNumSeq generates no candidate!!")
                    numOnlyMode = true
                    currentCombinations = setOf()
                }
            }
        } else {
            d("NumOnlyMode!")
        }
        d("after pushing [$num${configurations.pad[num].chars}]: seq${currentNumSeq}comb${currentCombinations}cands$currentCandidates")
    }

    fun flush() {
        currentNumSeq.clear()
        currentCombinations = setOf()
        currentCandidates = setOf()
        numOnlyMode = false
    }
}

/**
 * Cartesian multiply a Set of Strings with a Set of Chars
 *
 * <br/>i.e. Append each char to each string
 */
private operator fun Set<String>.times(chars: Set<Char>): Set<String> {
    val newSet = mutableSetOf<String>()
    if (size == 0)
        newSet.addAll(chars)
    else
        chars.forEach { char -> forEach { string -> newSet.add(string + char) } }
    return newSet
}

private fun MutableSet<String>.addAll(chars: Set<Char>) {
    chars.forEach { add(it.toString()) }
}

private fun String.decomposeVietnamese(): String {
    /* 774 0x306 COMBINING BREVE */
    val BREVE = '̆'
    /* 770 0x302 COMBINING CIRCUMFLEX ACCENT */
    val CIRCUMFLEX_ACCENT = '̂'
    /* 795 31B COMBINING HORN */
    val HORN = '̛'
    return Normalizer.normalize(this, Normalizer.Form.NFKD).replace((
            "([aA][$BREVE$CIRCUMFLEX_ACCENT])|([uUoO]$HORN)|[oO]$CIRCUMFLEX_ACCENT").toRegex()) {
//        when(it.value) {
//            "ă" -> "ă"
//            "Ă" -> "Ă"
//            "â" -> "â"
//            "Â" -> "Â"
//        }
        Normalizer.normalize(it.value, Normalizer.Form.NFKC)
    }
}

private fun String.composeVietnamese() = Normalizer.normalize(this, Normalizer.Form
        .NFKC)

/**
 * Promise to provide engine after initializing
 */
class EnginePromise(private val t9Engine: T9Engine, val context: Context) : Exception() {
    fun initializeThenGetBlocking(): T9Engine {
        t9Engine.initialize(context)
        return t9Engine
    }

}

var viVNEngine: T9Engine? = null
var enUSEngine: T9Engine? = null

// TODO Asynchronize this
fun Context.getEngineFor(locale: String): T9Engine {
    return when (locale) {
        LOCALE_VN -> viVNEngine ?: run {
            viVNEngine = T9Engine(this, locale)
            viVNEngine!!
        }
        LOCALE_US -> enUSEngine ?: run {
            enUSEngine = T9Engine(this, locale)
            enUSEngine!!
        }
        else -> throw UnsupportedOperationException()

    }
}