package com.vutrankien.t9vietnamese

import android.content.Context
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import java.util.*

class T9Engine @Throws(SnappydbException::class)
constructor(context: Context, locale: String) {
    val snappydb = DBFactory.open(context, locale)
    init {
        snappydb.put("24236", hashSetOf("chào"))
    }

    fun candidates(s: String): Set<String> {
        return snappydb.get(s,HashSet<String>().javaClass)
    }
}
