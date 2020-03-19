package com.vutrankien.t9vietnamese.android

import android.content.Context
import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import dagger.Component
import dagger.Module
import dagger.Provides
import java.text.Normalizer


@Component(modules = [PresenterModule::class, EngineModule::class, AndroidLogModule::class, EnvModule::class])
interface ActivityComponent {
    fun inject(service: T9Vietnamese)
    fun inject(activity: MainActivity)

}

@Module
class PresenterModule() {
    @Provides
    fun getSeed(): Lazy<Sequence<String>> {
        return lazy {
            val sortedWords = sortedSetOf<String>() // use String's "natural" ordering
            javaClass.classLoader.getResourceAsStream("vi-DauMoi.dic").bufferedReader().useLines {
                it.forEach { line ->
                    sortedWords.add(line.decomposeVietnamese())
                }
            }
            sortedWords.asSequence()
        }
    }

    @Provides
    fun presenter(
        engine: T9Engine,
        lg: LogFactory,
        env: Env
    ): Presenter {
        engine.pad = VnPad
        return Presenter(getSeed(), engine, env, lg)
    }
}

@Module
class EnvModule(private val context: Context) {
    @Provides
    fun env(): Env =
        AndroidEnv(context)
}

@Module
class AndroidLogModule {
    @Provides
    fun logFactory(): LogFactory =
        AndroidLogFactory()
}


/* 774 0x306 COMBINING BREVE */
private const val BREVE = '̆'
/* 770 0x302 COMBINING CIRCUMFLEX ACCENT */
private const val CIRCUMFLEX_ACCENT = '̂'
/* 795 31B COMBINING HORN */
private const val HORN = '̛'
/* 803 323 COMBINING DOT BELOW */
private const val DOT_BELOW = '̣'

fun String.decomposeVietnamese(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFKD)
        // rearrange intonation and vowel-mark order.
        .replace("([eE])$DOT_BELOW$CIRCUMFLEX_ACCENT".toRegex(), "$1$CIRCUMFLEX_ACCENT$DOT_BELOW")
        // recombine specific vowels.
        .replace(
            ("([aA][$BREVE$CIRCUMFLEX_ACCENT])|([uUoO]$HORN)|[oOeE]$CIRCUMFLEX_ACCENT").toRegex()
        ) { Normalizer.normalize(it.value, Normalizer.Form.NFKC)}

}
