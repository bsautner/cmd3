package com.app.di

import com.app.TerminalMain
import dagger.Module

@Module
class AppModule {

    fun provideTerminalMain() : TerminalMain {
        return TerminalMain()
    }

}