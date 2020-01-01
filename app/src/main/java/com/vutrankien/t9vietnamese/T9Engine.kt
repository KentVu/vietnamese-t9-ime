package com.vutrankien.t9vietnamese

import android.content.Context
import timber.log.Timber.d
import timber.log.Timber.w
import java.io.Closeable
import java.text.Normalizer

private val log = KLog("T9Engine")

val LOCALE_VN = "vi-VN"
val LOCALE_US = "en-US"

class T9Engine @Throws(EngineUninitializedException::class)
constructor(locale: String, val dbWrapper: DBWrapper): Closeable {
    private val configuration: Configuration = configurations[locale] ?:
        throw UnsupportedOperationException(UNSUPPORTED_MSG(locale))

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

    override fun close() {
            dbWrapper.close()
    }

    private fun <E> MutableList<E>.push(num: E) = add(num)

    fun input(num: Char) {
        currentNumSeq.push(num)
        if (!numOnlyMode) {
            currentCombinations *= (configuration.pad[num].chars)
            // filter combinations
            if (currentNumSeq.size > 1) {
                // Only start from 2 numbers and beyond
                _currentCandidates = dbWrapper.existingPrefix(currentCombinations)
                if (_currentCandidates.isNotEmpty())
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
        d("after pushing [$num${configuration.pad[num].chars}]: seq${currentNumSeq}comb${currentCombinations}cands$currentCandidates")
    }

    fun flush() {
        currentNumSeq.clear()
        currentCombinations = setOf()
        currentCandidates = setOf()
        numOnlyMode = false
    }

    fun candidatesFor(input: String): Set<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

/* 774 0x306 COMBINING BREVE */
const private val BREVE = '̆'
/* 770 0x302 COMBINING CIRCUMFLEX ACCENT */
const private val CIRCUMFLEX_ACCENT = '̂'
/* 795 31B COMBINING HORN */
const private val HORN = '̛'
/* 803 323 COMBINING DOT BELOW */
const private val DOT_BELOW = '̣'

internal fun String.decomposeVietnamese(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFKD)
            // rearrange intonation and vowel-mark order.
            .replace("([eE])$DOT_BELOW$CIRCUMFLEX_ACCENT".toRegex(), "$1$CIRCUMFLEX_ACCENT$DOT_BELOW")
            // recombine specific vowels.
            .replace(
                ("([aA][$BREVE$CIRCUMFLEX_ACCENT])|([uUoO]$HORN)|[oOeE]$CIRCUMFLEX_ACCENT").toRegex()
            ) {Normalizer.normalize(it.value, Normalizer.Form.NFKC)}

}

private fun String.composeVietnamese() = Normalizer.normalize(this, Normalizer.Form
        .NFKC)

/**
 * Promise to provide engine after initializing
 */
class EngineUninitializedException() : Exception
("The engine is not initialized!") {

}

// It knows all the words of a language
class T9Wordlist(val context: Context, private val db: TrieDB, val locale: String) {

    fun writeToDb(db: DBWrapper) {
        log.i("Destroying malicious database and reopen it!")
        db.clear()
        val inputStream = context.assets.open(configurations.getValue(locale).wordListFile)
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
                db.putAll(
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
        db.putMagic()
    }
    fun initializeThenGetBlocking(): T9Engine {
//        initialize(db)
        writeToDb(db)
        return T9Engine(locale, db)
    }

}

private var viVNEngine: T9Engine? = null
private var enUSEngine: T9Engine? = null

fun Context.getEngineFor(locale: String): T9Engine {
    val dbWrapper = TrieDB(filesDir)
    return when (locale) {
        LOCALE_VN -> viVNEngine ?: run {
            if (dbWrapper.haveMagic()) {
                log.i("DB OK!")
                viVNEngine = T9Engine(locale, dbWrapper)
                viVNEngine!!
            } else {
                throw EngineUninitializedException()
            }
        }
        LOCALE_US -> enUSEngine ?: run {
            enUSEngine = T9Engine(locale, dbWrapper)
            enUSEngine!!
        }
        else -> throw UnsupportedOperationException()

    }
}
