package com.vutrankien.t9vietnamese.android

import android.content.res.Resources
import com.vutrankien.t9vietnamese.lib.Seed

class DecomposedSeed(val resources: Resources) : Seed {

    override fun sequence(): Sequence<String> {
        return sequence {
            resources.assets.open("decomposed.dic.sorted").bufferedReader().useLines { lines ->
                lines.forEach { yield(it) }
            }
        }
    }

}
