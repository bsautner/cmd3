package com.jediterm.app

import com.jediterm.app.data.H2
import com.jediterm.terminal.command.CommandListener
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger

object Application {


    @JvmStatic
    fun main(arg: Array<String>) {
        BasicConfigurator.configure()
        Logger.getRootLogger().level = Level.INFO
        val h2 = H2()
        h2.connect()
        val app = App()
        app.launch()

    }

    private class App : CommandListener {
        private lateinit var term : JediTerm
        fun launch() {

            term = JediTermMain.launch(this)
        }

        override fun commandEntered(command: String) {
            println("Command Entered $command")
        }

        override fun commandSelected(command: String) {
            println("Command Selected $command")
            term.sendCommand(command)
        }

    }
}