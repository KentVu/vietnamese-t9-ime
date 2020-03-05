package com.vutrankien.t9vietnamese

import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.engine.T9Engine
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [EngineModule::class])
abstract class EngineComponents {
    //abstract fun logGenerator(): LogGenerator
    abstract fun engine(): T9Engine
}

@Module
class EngineModule {
    @Provides
    fun engine(lg: LogGenerator): T9Engine = DefaultT9Engine(lg)
}

@Component(modules = [PresenterModule::class])
abstract class PresenterComponents {
    abstract fun presenter(): Presenter
}

@Module
class PresenterModule(
    private val engineSeed: Sequence<String>,
    private val engine: T9Engine
) {
    @Provides
    fun presenter(
        lg: LogGenerator
    ): Presenter = Presenter(engineSeed, engine, lg)
}
