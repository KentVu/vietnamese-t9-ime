package com.vutrankien.t9vietnamese

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import org.jetbrains.anko.db.*
import org.trie4j.patricia.MapPatriciaTrie
import timber.log.Timber
import timber.log.Timber.i
import java.io.*

interface DBWrapper : Closeable {
    fun clear()
    fun put(key: String, value: Int)
    fun get(key: String): Int?
    override fun close()
    fun haveMagic(): Boolean
    fun putAll(keys: List<String>, defaultValue: Int = 0)
    fun existingPrefix(prefixes: Set<String>): Set<String>
    fun putMagic()
}

private val MAGIC:Int = 0xA11600D

class TrieDB(file: File) : DBWrapper {

    private val TRIE_FILENAME = "trie"
//    val trie = MapPatriciaTrie<Int>()
    val trie : MapPatriciaTrie<Int>

    private val trieFile = file

    init {
        // The java deserialization thing, y'know :|
        @Suppress("UNCHECKED_CAST")
        trie =
                try {
                    ObjectInputStream(FileInputStream(trieFile))
                            .use { it.readObject() as MapPatriciaTrie<Int> }
                } catch (ex: Exception) {
                    MapPatriciaTrie()
                }
    }

    override fun clear() {
        trieFile.delete()
    }

    override fun put(key: String, value: Int) {
        trie.insert(key, value)
    }

    override fun get(key: String): Int? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        ObjectOutputStream(FileOutputStream(trieFile)).writeObject(trie)
    }

    private val MAGIC_KEY = "HELO"

    override fun haveMagic(): Boolean {
        return trie.get(MAGIC_KEY) == MAGIC
    }

    override fun putAll(keys: List<String>, defaultValue: Int) {
        keys.forEach { put(it, defaultValue) }
    }

    override fun existingPrefix(prefixes: Set<String>): Set<String> =
            prefixes.flatMap {
                trie.predictiveSearch(it)
            }.toSet()

    override fun putMagic() {
        put(MAGIC_KEY, MAGIC)
    }

    fun readFrom(wordList: Wordlist) {
        i("Destroying malicious database and reopen it!")
        clear()
        var step = 0
        var bytesRead = 0
        val flength = wordList.bytesCount()
        wordList.forEachGroup(200) {
            putAll(
                    it.map {
                        it.decomposeVietnamese()
                    }
            )
            val lastStep = step
            bytesRead += it.fold(0) { i, s ->
                i + s.toByteArray().size + 1
            }
            step = (bytesRead / (flength / 100))
            if (lastStep != step) Timber.d("Written $bytesRead / $flength to db ($step%)")
        }
    }

    val initialized: Boolean
            get() = haveMagic()

}

class T9SqlHelper(ctx: Context, dbname: String) : ManagedSQLiteOpenHelper(ctx, dbname), DBWrapper {
    companion object {

        private val DEFAULT_LIMIT = 5

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

    override fun haveMagic(): Boolean {
        return get("HELO") == MAGIC
    }

    override fun clear() {
//        database.deleteTable()
        clearTable()
    }

    override fun putMagic() {
        put("HELO", MAGIC)
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

class SnappyDBWrapper(private val context: Context, private val locale: String) : DBWrapper {
    val snappydb = DBFactory.open(context, locale)

    init {
    }

    override fun clear() {
        snappydb.destroy()
        snappydb.close()
        // initialize database
        val newWrapper = SnappyDBWrapper(context, locale)
        newWrapper.put("HELO", MAGIC)
    }

    override fun haveMagic(): Boolean {
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

    override fun putMagic() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun existingPrefix(prefixes: Set<String>): Set<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        snappydb.close()
    }

}