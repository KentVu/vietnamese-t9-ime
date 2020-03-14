package com.vutrankien.t9vietnamese.lib

import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.engine.T9Engine
import dagger.Component
import dagger.Module
import dagger.Provides
import java.io.File

@Component(modules = [EngineModule::class, LogModule::class, EnvModule::class])
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
class EngineModule(private val env: Env) {
    @Provides
    fun engine(lg: LogFactory): T9Engine = DefaultT9Engine(lg, env)
}

@Component(modules = [PresenterModule::class, LogModule::class])
abstract class PresenterComponents {
    abstract fun presenter(): Presenter
}

@Module
class PresenterModule(
        private val engineSeed: Sequence<String>,
        private val engine: T9Engine,
        private val env: Env
) {
    @Provides
    fun presenter(
        lg: LogFactory
    ): Presenter =
        Presenter(lazy { engineSeed }, engine, env, lg)
}

@Module
class EnvModule() {
    @Provides
    fun env(): Env =
            object : Env {
                override fun fileExists(file: String): Boolean =
                        File(file).exists()
            }
}
