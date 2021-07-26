package com.vutrankien.t9vietnamese.lib

import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.engine.T9Engine
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import java.io.File


@Component(modules = [EnvModule::class, LogModule::class])
abstract class EnvComponent {
    abstract val lg: LogFactory
    abstract val engineComponentBuilder: EngineComponent.Builder
}

@Module(subcomponents = [EngineComponent::class])
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
        fun engineModule(em: EngineModule): Builder
        fun build(): EngineComponent
    }
}

@Module
abstract class EngineModule() {
    @Provides
    fun engine(
            lg: LogFactory,
            env: Env,
            pad: PadConfiguration,
            seed: Sequence<String>
    ): T9Engine = DefaultT9Engine(seed, pad, lg, TrieDb(lg, env))
}

@Component(modules = [PresenterModule::class, LogModule::class])
abstract class PresenterComponents {
    abstract fun presenter(): Presenter
}

@Module
class PresenterModule(
        private val engine: T9Engine,
        private val env: Env
) {
    @Provides
    fun presenter(
        lg: LogFactory
    ): Presenter =
        Presenter(engine, env, lg)
}

@Module
class ConfigurationModule(private val pad: PadConfiguration) {
    @Provides
    fun padConfiguration(): PadConfiguration =
            pad
}
