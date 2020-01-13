package com.vutrankien.t9vietnamese

import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.text.Normalizer

// TODO Have Dagger inject this
private val log:Logging = JavaLog("T9Engine")

class OldT9Engine @Throws(EngineUninitializedException::class)
constructor(locale: String, val db: DB): Closeable {

    private val configuration: Configuration = configurations[locale] ?:
        throw UnsupportedOperationException(UNSUPPORTED_MSG(locale))

    private val currentNumSeq = mutableListOf<Key>()
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
            db.close()
    }

    private fun <E> MutableList<E>.push(num: E) = add(num)

    fun input(key: Key) {
        currentNumSeq.push(key)
        if (!numOnlyMode) {
            currentCombinations *= (configuration.pad[key].chars)
            // filter combinations
            if (currentNumSeq.size > 1) {
                // Only start from 2 numbers and beyond
                _currentCandidates = db.existingPrefix(currentCombinations)
                if (_currentCandidates.isNotEmpty())
                    currentCombinations = currentCombinations.filter { comb ->
                            _currentCandidates.any { it.startsWith(comb) }
                        }.toSet()
                else {
                    log.w("seq$currentNumSeq generates no candidate!!")
                    numOnlyMode = true
                    currentCombinations = setOf()
                }
            }
        } else {
            log.d("NumOnlyMode!")
        }
        log.d("after pushing [$key${configuration.pad[key].chars}]: seq${currentNumSeq}comb${currentCombinations}cands$currentCandidates")
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

private fun String.composeVietnamese() = Normalizer.normalize(this, Normalizer.Form
        .NFKC)

/**
 * Promise to provide engine after initializing
 */
class EngineUninitializedException() : Exception
("The engine is not initialized!") {

}

// It knows all the words of a language
class T9Wordlist(val inputStream: InputStream, private val db: TrieDB, val locale: String) {

    fun writeToDb(db: DB) {
        log.i("Destroying malicious database and reopen it!")
        db.clear()
        //val inputStream = context.assets.open(com.vutrankien.t9vietnamese.configurations.getValue(locale).wordListFile)
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
                if (lastStep != step) log.d("Written $bytesRead / $flength to db ($step%)")
            }
        }
        // put magic at last to mark database stable (avoid app crash)
        db.putMagic()
    }
    fun initializeThenGetBlocking(): OldT9Engine {
//        initialize(db)
        writeToDb(db)
        return OldT9Engine(locale, db)
    }

}

private var viVNEngine: OldT9Engine? = null
private var enUSEngine: OldT9Engine? = null

fun getEngineFor(filesDir: File, locale: String): OldT9Engine {
    val dbWrapper = TrieDB(filesDir)
    return when (locale) {
        LOCALE_VN -> viVNEngine ?: run {
            if (dbWrapper.haveMagic()) {
                com.vutrankien.t9vietnamese.log.i("DB OK!")
                viVNEngine = OldT9Engine(locale, dbWrapper)
                viVNEngine!!
            } else {
                throw EngineUninitializedException()
            }
        }
        LOCALE_US -> enUSEngine ?: run {
            enUSEngine = OldT9Engine(locale, dbWrapper)
            enUSEngine!!
        }
        else -> throw UnsupportedOperationException()

    }
}
