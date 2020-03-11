package com.vutrankien.t9vietnamese.android

import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.EngineModule
import com.vutrankien.t9vietnamese.lib.LogFactory
import com.vutrankien.t9vietnamese.lib.Presenter
import com.vutrankien.t9vietnamese.lib.VnPad
import dagger.Component
import dagger.Module
import dagger.Provides


@Component(modules = [PresenterModule::class, EngineModule::class, AndroidLogModule::class])
interface ActivityComponent {
    fun inject(service: T9Vietnamese)
    fun inject(activity: MainActivity)

}

@Module
class PresenterModule {
    @Provides
    fun getSeed(): Sequence<String> {
        val sortedWords = sortedSetOf<String>() // use String's "natural" ordering
        javaClass.classLoader.getResourceAsStream("vi-DauMoi.dic").bufferedReader().useLines {
            it.forEach { line ->
                sortedWords.add(line)
            }
        }
        return sortedWords.asSequence()
    }

    @Provides
    fun presenter(
        seed: Sequence<String>,
        engine: T9Engine,
        lg: LogFactory
    ): Presenter {
        engine.pad = VnPad
        return Presenter(seed, engine, lg)
    }
}

@Module
class AndroidLogModule {
    @Provides
    fun logFactory(): LogFactory =
        AndroidLogFactory()
}
