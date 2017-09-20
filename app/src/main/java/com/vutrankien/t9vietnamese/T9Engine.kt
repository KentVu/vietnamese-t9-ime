package com.vutrankien.t9vietnamese

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import org.intellij.lang.annotations.Pattern
import org.jetbrains.anko.db.*
import timber.log.Timber

private val log = KLog("T9Engine")

private val MAGIC:Int = 0xA11600D
val LOCALE_VN = "vi-VN"
val LOCALE_US = "en-US"

class T9Engine @Throws(EnginePromise::class)
constructor(context: Context, locale: String): AutoCloseable {
    var dbWrapper : DBWrapper = T9SqlHelper(context)


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
        val inputStream = context.assets.open("morphemes.txt")
        inputStream.bufferedReader().forEachLine { dbWrapper.put(it, 0) }
    }

    override fun close() {
        dbWrapper.close()
    }

    fun candidates(numseq: String): Set<String> {

//        val keys = dbWrapper.findKeys(numseq)

//        return keys.map {
//            numseq2word(it)
//        }.filter { it != "" }.toSet()
//        return dbWrapper.findKeys(numseq2word(numseq)).toSet()
        return with(numseq2word(numseq)){
            this?.let {
                dbWrapper.findKeys(this).toSet()
            } ?: emptySet()
        }
    }

    private fun numseq2word(@Pattern(value= """^\d+""") numseq: String): String? {
        /* TODO #3 */
//        mapOf<String, String>("2" to "abc")
        return mapOf<String, String>("24236" to "chào").get(numseq)
    }
}


class T9SqlHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "t9vietnamese"), DBWrapper {

    companion object {
        private var instance: T9SqlHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): T9SqlHelper {
            if (instance == null) {
                instance = T9SqlHelper(ctx.applicationContext)
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

abstract class SqliteDBWrapper(private val context: Context, locale: String) : DBWrapper {
    private val database: T9SqlHelper
        get() = T9SqlHelper.getInstance(context.applicationContext)
}

class EnginePromise(private val t9Engine: T9Engine, val context: Context) : Exception() {
    fun getBlocking(): T9Engine {
        t9Engine.initialize(context)
        return t9Engine
    }

}

interface DBWrapper {
    fun recreate(): DBWrapper
    fun findKeys(key: String): Array<String>
    fun put(key: String, value: Int)
    fun get(key: String): Int?
    fun close()
    fun isDbValid(): Boolean

}

class SnappyDBWrapper(private val context: Context, private val locale: String): DBWrapper {

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

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(key: String): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun put(key: String, value: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findKeys(key: String): Array<String> {
        return snappydb.findKeys(key)
    }

    var snappydb = DBFactory.open(context, locale)

    init {
    }

    override fun recreate(): DBWrapper {
        destroy()
        // initialize database
        val newWrapper = SnappyDBWrapper(context, locale)
        newWrapper.put("HELO", MAGIC)
        return newWrapper
    }

    private fun destroy() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

data class PadConfiguration(
        val num1: Set<Char>,
        val num2: Set<Char>,
        val num3: Set<Char>,
        val num4: Set<Char>,
        val num5: Set<Char>,
        val num6: Set<Char>,
        val num7: Set<Char>,
        val num8: Set<Char>,
        val num9: Set<Char>,
        val num0: Set<Char>
)
//object StandardConfiguration: PadConfiguration(num1 = setOf('a', 'b', 'c'))
//val StandardConfiguration = PadConfiguration(num1 = setOf('a', 'b', 'c'))
val configurations =
        mapOf<String, PadConfiguration>(
                LOCALE_VN to PadConfiguration(
                        num2 = linkedSetOf('a', 'ă', 'â', 'b', 'c'),
                        num3 = linkedSetOf('d', 'đ', 'e', 'ê', 'f'),
                        num4 = linkedSetOf('g', 'h', 'i', 'ê', 'f'),
                        num5 = linkedSetOf('j', 'k', 'l', 'ê', 'f'),
                        num6 = linkedSetOf('m', 'n', 'o', 'ê', 'f'),
                        num7 = linkedSetOf('p', 'q', 'r', 's', 'f'),
                        num8 = linkedSetOf('t', 'u', 'v', 'ê', 'f'),
                        num9 = linkedSetOf('w', 'x', 'y', 'z', 'f'),
                        num0 = linkedSetOf('d', 'đ', 'e', 'ê', 'f'),
                        num1 = linkedSetOf('d', 'đ', 'e', 'ê', 'f')
                )
        )

class T9EngineFactory(context: Context) {
    val viVNEngine: T9Engine by lazy { T9Engine(context, LOCALE_VN) }
    val enUSEngine: T9Engine by lazy { T9Engine(context, LOCALE_US) }
}

//val viVNEngine: T9Engine by LazyT9Engine(context, "vi-VN")

var viVNEngine: T9Engine? = null
var enUSEngine: T9Engine? = null

// TODO Asynchronize this
fun Context.getEngineFor(locale: String): T9Engine {
    return when (locale) {
        LOCALE_VN -> viVNEngine ?: run {
            viVNEngine = T9Engine(this, LOCALE_VN)
            viVNEngine!!
        }
        LOCALE_US -> enUSEngine ?: run {
            enUSEngine = T9Engine(this, LOCALE_US)
            enUSEngine!!
        }
        else -> throw UnsupportedOperationException()

//            if (viVNEngine != null) viVNEngine!! else {
//                viVNEngine = T9Engine(this, "vi-VN"); viVNEngine!!
//            }

    }
}

// Access property for Context
//private val Context.t9database: T9SqlHelper
//    get() = T9SqlHelper.getInstance(applicationContext)