package com.vutrankien.t9vietnamese.android

import android.content.Context
import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import dagger.Component
import dagger.Module
import dagger.Provides


@Component(modules = [PresenterModule::class, EngineModule::class, AndroidLogModule::class, EnvModule::class])
interface ActivityComponent {
    fun inject(service: T9Vietnamese)
    fun inject(activity: MainActivity)

}

@Module
class PresenterModule() {

    @Provides
    fun presenter(
        engine: T9Engine,
        lg: LogFactory,
        env: Env
    ): Presenter {
        // TODO: Make this configurable
        engine.pad = VnPad
        return Presenter(VietnameseWordSeed.getFromJavaResource(), engine, env, lg)
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


