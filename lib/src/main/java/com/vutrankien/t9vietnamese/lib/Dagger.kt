package com.vutrankien.t9vietnamese.lib

import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.engine.T9Engine
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import java.io.File


@Component(modules = [LogModule::class, EnvModule::class])
abstract class EnvComponent {
    abstract val lg: LogFactory
}

@Module(subcomponents = [EngineComponent::class])
class LogModule {
    @Provides
    fun logFactory(): LogFactory =
            JavaLogFactory()
}

@Subcomponent(modules = [ConfigurationModule::class, EngineModule::class])
abstract class EngineComponent {
    abstract fun engine(): T9Engine
    @Subcomponent.Builder
    interface Builder {
        fun configurationModule(cm: ConfigurationModule): Builder
        fun build(): EngineComponent
    }
}

@Module
class EngineModule() {
    @Provides
    fun engine(
            lg: LogFactory,
            env: Env,
            pad: PadConfiguration
    ): T9Engine = DefaultT9Engine(lg, env, pad = pad)
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
        Presenter(engineSeed, engine, env, lg)
}

@Module
class EnvModule() {
    @Provides
    fun env(): Env =
            object : Env {
                override fun fileExists(path: String): Boolean =
                        File(path).exists()

                override val workingDir: String
                    get() = "."
            }
}

@Module
class ConfigurationModule(private val pad: PadConfiguration) {
    @Provides
    fun padConfiguration(): PadConfiguration =
            pad
}
