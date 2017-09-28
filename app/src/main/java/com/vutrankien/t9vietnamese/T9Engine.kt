package com.vutrankien.t9vietnamese

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import org.intellij.lang.annotations.Pattern
import org.jetbrains.anko.db.*
import timber.log.Timber
import timber.log.Timber.d
import timber.log.Timber.w
import java.io.Closeable
import java.text.Normalizer
import java.util.*

private val log = KLog("T9Engine")

private val MAGIC:Int = 0xA11600D
val LOCALE_VN = "vi-VN"
val LOCALE_US = "en-US"

class T9Engine @Throws(EnginePromise::class)
constructor(context: Context, locale: String): Closeable {
    val configurations = Configuration(locale)
    var dbWrapper : DBWrapper = T9SqlHelper(context, configurations.dbname)

    init {
        if (dbWrapper.isDbValid()) {
            log.i("DB OK!")
        } else {
//            initialize()
            throw EnginePromise(this, context)
        }
    }

    fun initialize(context: Context) {
        log.i("Destroying malicious database and reopen it!")
        dbWrapper = dbWrapper.recreate()
//        val fd = context.assets.openFd("morphemes.txt")
        val inputStream = context.assets.open(configurations.wordListFile)
//        val inputStream = fd.createInputStream()
        var step = 0
        var bytesRead = 0
        // https://stackoverflow.com/a/6992255
        val flength = inputStream.available()
//        val flength = fd.length
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
    }

    override fun close() {
        dbWrapper.close()
    }

    fun input(numseq: String): Unit {
        numseq.forEach { input(it) }
        numseq2word(numseq)?.let {
            dbWrapper.findKeys(it).toSet()
        } ?: emptySet()
    }

    private val currentNumSeq = mutableListOf<Char>()
    private val currentCandidates = mutableSetOf<String>()

    private var currentCombinations = setOf<String>()

    private fun input(num: Char) {
        currentNumSeq.push(num)
//        currentCandidates.addAll(configurations.pad[num].chars)
//        currentCandidates.cartesianMul(configurations.pad[num].chars)
        currentCombinations *= (configurations.pad[num].chars)
        // filter combinations
        if (currentNumSeq.size > 1) {
//            currentCombinations.forEach { dbWrapper.findKeys(...) }
            val existingPrefix = dbWrapper.existingPrefix(currentCombinations)
            currentCombinations = currentCombinations.filter { comb ->
                existingPrefix.any { it.startsWith(comb) }
            }.toSet()
//            currentNumSeq.cartesianMul(0,1)
        }
//        configurations.pad[num].chars.toS
    }

    fun currentCandidates(): Set<String> {
        TODO()
    }

    private fun numseq2word(
            @Pattern(value= """^\d+""") numseq: String
    ): String? {
        /* TODO #3 */
        numseq.fold(StringBuilder()) { sb, num ->
            val chars = configurations.pad[num].chars
            chars.forEach { sb.append("$it%") }
            sb
        }
        return mapOf<String, String>("24236" to "chào").get(numseq)
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

private fun Set<String>.addAll(chars: Set<Char>): MutableSet<String> {
    val newSet = toMutableSet()
    chars.forEach { newSet.add(it.toString()) }
    return newSet
}

private fun MutableSet<String>.addAll(chars: Set<Char>) {
    chars.forEach { add(it.toString()) }
}

private fun <E> MutableList<E>.push(num: E) = add(num)

private fun String.decomposeVietnamese(): String {
    /* 774 0x306 COMBINING BREVE */
    val BREVE = '̆'
    /* 770 0x302 COMBINING CIRCUMFLEX ACCENT */
    val CIRCUMFLEX_ACCENT = '̂'
    /* 795 31B COMBINING HORN */
    val HORN = '̛'
    return Normalizer.normalize(this, Normalizer.Form.NFKD).replace(("" +
            "([aA][$BREVE$CIRCUMFLEX_ACCENT])|([uUoO]$HORN)").toRegex()) {
//        when(it.value) {
//            "ă" -> "ă"
//            "Ă" -> "Ă"
//            "â" -> "â"
//            "Â" -> "Â"
//        }
        Normalizer.normalize(it.value, Normalizer.Form.NFKC)
    }
}

class T9SqlHelper(ctx: Context, dbname: String) : ManagedSQLiteOpenHelper(ctx, dbname), DBWrapper {
    companion object {

        private val DEFAULT_LIMIT = 10

        private var instance: T9SqlHelper? = null

        @Synchronized
        fun getInstance(ctx: Context, dbname: String): T9SqlHelper {
            if (instance == null) {
                instance = T9SqlHelper(ctx.applicationContext, dbname)
            }
            return instance!!
        }
    }

    private val TABLE_NAME = "WordFreq"
    private val COLUMN_WORD = "word"
    private val COLUMN_FREQ = "freq"

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(TABLE_NAME, true,
                "_id" to INTEGER + PRIMARY_KEY,
                COLUMN_WORD to TEXT + UNIQUE,
                COLUMN_FREQ to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    override fun put(key: String, value: Int) {
        use {
            insert(TABLE_NAME,
                    COLUMN_WORD to key,
                    COLUMN_FREQ to value)
        }
    }

    override fun putAll(keys: List<String>, defaultValue: Int) {
        use {
            transaction {
                keys.forEach { key ->
                    put(key, defaultValue)
                }
            }
        }
    }

    override fun get(key: String): Int? {
        return use {
            select(TABLE_NAME).column(COLUMN_FREQ).whereArgs("$COLUMN_WORD = '$key'")
                    .parseOpt(IntParser)
        }
    }

    @SuppressLint("Recycle")
    override fun existingPrefix(prefixes: Set<String>): Set<String> {
        val selects = prefixes.map {
            """SELECT $COLUMN_WORD
            FROM ( SELECT $COLUMN_WORD
                    FROM $TABLE_NAME
                    WHERE $COLUMN_WORD LIKE "$it%"
                    ORDER BY $COLUMN_FREQ DESC
            LIMIT 10 )"""
        }
        val queryBuilder = SQLiteQueryBuilder()
        val sql = queryBuilder.buildUnionQuery(selects.toTypedArray(), null, null)
        return use {
            rawQuery(sql, null).parseList(StringParser).toSet()
        }
    }

    override fun isDbValid(): Boolean {
        return get("HELO") == MAGIC
    }

    override fun recreate(): DBWrapper {
//        database.deleteTable()
        clearTable()
        put("HELO", MAGIC)
        return this
    }

    override fun findKeys(prefix: String): Array<String> {
        return use {
            select(TABLE_NAME, COLUMN_WORD)
                    .whereArgs("$COLUMN_WORD LIKE '$prefix%'")
                    .orderBy(COLUMN_FREQ).limit(DEFAULT_LIMIT)
                    .parseList(StringParser)
                    .toTypedArray()
        }
    }

    fun deleteTable() {
        use {
            //            delete("WordFreq")
            dropTable(TABLE_NAME)
            onCreate(this)
        }
    }

    fun clearTable() {
        use {
            delete(TABLE_NAME)
        }
    }

}

class EnginePromise(private val t9Engine: T9Engine, val context: Context) : Exception() {
    fun getBlocking(): T9Engine {
        t9Engine.initialize(context)
        return t9Engine
    }

}

interface DBWrapper : Closeable {
    fun recreate(): DBWrapper
    fun findKeys(prefix: String): Array<out String>
    fun put(key: String, value: Int)
    fun get(key: String): Int?
    override fun close()
    fun isDbValid(): Boolean
    fun putAll(keys: List<String>, defaultValue: Int = 0)
    fun existingPrefix(prefixes: Set<String>): Set<String>
}

class SnappyDBWrapper(private val context: Context, private val locale: String) : DBWrapper {
    val snappydb = DBFactory.open(context, locale)

    init {
    }

    override fun recreate(): DBWrapper {
        snappydb.destroy()
        snappydb.close()
        // initialize database
        val newWrapper = SnappyDBWrapper(context, locale)
        newWrapper.put("HELO", MAGIC)
        return newWrapper
    }

    override fun isDbValid(): Boolean {
        // powerful magic entry to check if were talking to the right database
        val magicEntry = try {
            get("HELO")
        } catch (e: SnappydbException) {
//            Timber.e(e.message)
//            Timber.e(Exception(e))
            Timber.w(e)
            ""
        }
        if (magicEntry != MAGIC) {
            return false
        }
        return true
    }

    override fun get(key: String): Int {
        return snappydb.getInt(key)
    }

    override fun put(key: String, value: Int) {
        snappydb.put(key, value)
    }

    override fun putAll(keys: List<String>, defaultValue: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findKeys(prefix: String): Array<out String> = try {
        snappydb.findKeys(prefix)!!
    } catch (e: Exception) {
        w(e)
        emptyArray<String>()
    }

    override fun existingPrefix(prefixes: Set<String>): Set<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        snappydb.close()
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