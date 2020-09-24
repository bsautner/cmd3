package com.app

import com.app.data.H2
import com.jediterm.app.SelectionListener
import com.jediterm.app.SplitPaneDemo
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import javax.swing.JSplitPane

object Application {


    @JvmStatic
    fun main(arg: Array<String>) {
        BasicConfigurator.configure()
        Logger.getRootLogger().level = Level.INFO

        val app = App()
        app.launch()

    }

    private class App : SelectionListener  {
        private val dao = H2()
        private lateinit var term : JediTerm


        fun launch() {
            dao.connect()
            val jSplitPane : JSplitPane = SplitPaneDemo(this).splitPane
            term = JediTermMain.launch(jSplitPane)
        }

//        override fun commandEntered(command: String) {
//            println("Command Entered $command")
//            dao.insertCommand(command)
//        }
//
        override fun commandSelected(command: String) {
            println("Command Selected $command")
            term.sendCommand(command)
        }

    }
}