package com.vutrankien.t9vietnamese

import android.content.Context
import com.snappydb.DB
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import org.intellij.lang.annotations.Pattern
import timber.log.Timber
import timber.log.Timber.e
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private val log = KLog("T9Engine")

class T9Engine @Throws(SnappydbException::class)
constructor(context: Context, locale: String): AutoCloseable {
    var dbWrapper = DBWrapper(context, locale)

    init {
        if (dbWrapper.validate()) {
            log.i("DB OK!")
        } else {
            log.i("Destroying malicious database and reopen it!")
            dbWrapper = dbWrapper.recreate()
            dbWrapper.put("HELO", "On your mark, sir!")
//            dbWrapper.put("24236", arrayOf("chào"))
//            dbWrapper.put("chào", arrayOf("24236"))
            dbWrapper.put("chào", 0)
//            put("24236", arrayOf("chào"))
//            dbWrapper.put("chào", 0)
        }
    }

    override fun close() {
        dbWrapper.close()
    }

    fun candidates(numseq: String): Set<String> {
//        return dbWrapper.getArray(numseq,String::class.java)
//        return dbWrapper.

        val keys = dbWrapper.findKeys(numseq)

        return keys.map {
            try {
//                dbWrapper.get(numseq2word(it))
                numseq2word(it)
            } catch(e: SnappydbException) {
                // TODO delete this it's too paranoid!!!
                ""
            }
        }.filter { it != "" }.toSet()
    }

    private fun numseq2word(@Pattern(value= """^\d+""") numseq: String): String {
        /* TODO #3 */
        return mapOf<String, String>("24236" to "chào").get(numseq)!!
    }
}

class DBWrapper(private val context: Context, private val locale: String) : DB by DBFactory.open(context) {
//    var snappydb = DBFactory.open(context, locale)

    init {
    }

    fun recreate(): DBWrapper {
        destroy()
        val newWrapper = DBWrapper(context, locale)
        return newWrapper
    }

    fun validate(): Boolean {
        // powerful magic entry to check if were talking to the right database
        val magicEntry = try {
            get("HELO")
        } catch (e: SnappydbException) {
            e(e);""
        }
        if (magicEntry != "On your mark, sir!") {
            // initialize databasem
//            destroy()
//            snappydb = DBFactory.open(context, locale)
//            throw UnsupportedOperationException("Invalid database!!")
            return false
        }
        return true
    }

}

class LazyT9Engine(val ctx: Context, val locale: String): ReadOnlyProperty<T9Engine?, T9Engine> {
    operator override fun getValue(thisRef: T9Engine?, property: KProperty<*>): T9Engine = thisRef ?: T9Engine(ctx, locale)
}

class T9EngineFactory(context: Context) {
    val viVNEngine: T9Engine by lazy { T9Engine(context, LOCALE_VN) }
    val enUSEngine: T9Engine by lazy { T9Engine(context, LOCALE_US) }
}

//val viVNEngine: T9Engine by LazyT9Engine(context, "vi-VN")

var viVNEngine: T9Engine? = null
var enUSEngine: T9Engine? = null

val LOCALE_VN = "vi-VN"
val LOCALE_US = "en-US"

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