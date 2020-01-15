package com.vutrankien.t9vietnamese

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import org.jetbrains.anko.db.*
import timber.log.Timber

class T9SqlHelper(ctx: Context, dbname: String) : ManagedSQLiteOpenHelper(ctx, dbname), DB {
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

class SnappyDB(private val context: Context, private val locale: String) : DB {
    val snappydb = DBFactory.open(context, locale)

    init {
    }

    override fun clear() {
        snappydb.destroy()
        snappydb.close()
        // initialize database
        val newWrapper = SnappyDB(context, locale)
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