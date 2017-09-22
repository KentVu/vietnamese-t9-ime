package com.vutrankien.t9vietnamese

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import org.intellij.lang.annotations.Pattern
import org.jetbrains.anko.db.*
import timber.log.Timber
import timber.log.Timber.d
import timber.log.Timber.w
import java.io.Closeable

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
                dbWrapper.putAll(it)
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

    fun candidates(numseq: String): Set<String> {
//        return dbWrapper.findKeys(numseq2word(numseq)).toSet()

        return numseq2word(numseq)?.let {
            dbWrapper.findKeys(it).toSet()
        } ?: emptySet()
    }

    private fun numseq2word(
            @Pattern(value= """^\d+""") numseq: String
    ): String? {
        /* TODO #3 */
//        mapOf<String, String>("2" to "abc")

        return mapOf<String, String>("24236" to "chào").get(numseq)
    }
}

class T9SqlHelper(ctx: Context, dbname: String) : ManagedSQLiteOpenHelper(ctx, dbname), DBWrapper {
    companion object {

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

    override fun isDbValid(): Boolean {
        return get("HELO") == MAGIC
    }

    override fun recreate(): DBWrapper {
//        database.deleteTable()
        clearTable()
        put("HELO", MAGIC)
        return this
    }

    private val DEFAULT_LIMIT = 10

    override fun findKeys(key: String): Array<String> {
        return use {
            select(TABLE_NAME, COLUMN_WORD)
                    .whereArgs("$COLUMN_WORD LIKE '$key%'")
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
    fun findKeys(key: String): Array<out String>
    fun put(key: String, value: Int)
    fun get(key: String): Int?
    override fun close()
    fun isDbValid(): Boolean
    fun putAll(keys: List<String>, defaultValue: Int = 0)
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

    override fun findKeys(key: String): Array<out String> = try {
        snappydb.findKeys(key)!!
    } catch (e: Exception) {
        w(e)
        emptyArray<String>()
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