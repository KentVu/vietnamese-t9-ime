package com.vutrankien.t9vietnamese.android

import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
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
    private val seed: Sequence<String>
        get() {
            val sortedWords = sortedSetOf<String>() // use String's "natural" ordering
            javaClass.classLoader.getResourceAsStream("vi-DauMoi.dic").bufferedReader().useLines {
                it.forEach { line ->
                    //log.d(line)
                    sortedWords.add(line)
                }
            }
            return sortedWords.asSequence()
        }

    @Provides
    fun presenter(
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
