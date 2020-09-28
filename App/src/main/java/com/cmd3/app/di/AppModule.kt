package com.cmd3.app.di

import com.cmd3.app.TerminalMain
import dagger.Module

@Module
class AppModule {

    fun provideTerminalMain() : TerminalMain {
        return TerminalMain()
    }

}