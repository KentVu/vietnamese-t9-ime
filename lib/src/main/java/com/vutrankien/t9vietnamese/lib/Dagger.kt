package com.vutrankien.t9vietnamese.lib

import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.engine.T9Engine
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [EngineModule::class, LogModule::class])
abstract class EngineComponents {
    abstract val lg: LogFactory
    abstract fun engine(): T9Engine
}

@Module
class LogModule {
    @Provides
    fun logFactory(): LogFactory =
        JavaLogFactory()
}

@Module
class EngineModule {
    @Provides
    fun engine(lg: LogFactory): T9Engine = DefaultT9Engine(lg)
}

@Component(modules = [PresenterModule::class, LogModule::class])
abstract class PresenterComponents {
    abstract fun presenter(): Presenter
}

@Module
class PresenterModule(
    private val engineSeed: Sequence<String>,
    private val engine: T9Engine,
    val env: Env
) {
    @Provides
    fun presenter(
        lg: LogFactory
    ): Presenter =
        Presenter(lazy { engineSeed }, engine, env, lg)
}
