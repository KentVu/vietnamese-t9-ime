package com.vutrankien.t9vietnamese

import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [PresenterModule::class, LogModule::class])
interface ActivityComponent {
    fun inject(service: T9Vietnamese)

}

@Module
class LogModule {
    @Provides
    fun logFactory(): LogFactory = AndroidLogFactory()
}
