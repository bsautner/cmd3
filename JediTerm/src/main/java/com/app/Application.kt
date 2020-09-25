package com.app

import com.app.data.H2
import com.app.ux.CommandListPane
import com.app.ux.MainSplitPane
import com.app.ux.SelectionListener
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import javax.swing.JFrame
import javax.swing.JSplitPane

object Application {


    @JvmStatic
    fun main(arg: Array<String>) {
        BasicConfigurator.configure()
        Logger.getRootLogger().level = Level.INFO

        val app = App()
        app.launch()

    }

    private class App : SelectionListener {
        private val dao = H2()
        private lateinit var term : JediTermMain
        private lateinit var mainFrame : JFrame
        private lateinit var listFrame : CommandListPane

        fun launch() {
            dao.connect()
            mainFrame = JFrame()
            mainFrame.setSize(1200, 800)

            listFrame = CommandListPane(this)

            term = JediTermMain()

            val jSplitPane : JSplitPane = MainSplitPane().splitPane
            jSplitPane.rightComponent = term.myTerminal.component
            jSplitPane.leftComponent = listFrame
            jSplitPane.setDividerLocation(0.4)
         //   mainFrame.add(jSplitPane)
            mainFrame.pack()
            mainFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            mainFrame.contentPane.add(jSplitPane)
            //mainFrame.contentPane.add(term.myTerminal.component)
            mainFrame.pack()
            mainFrame.setLocationRelativeTo(null)
            mainFrame.isVisible = true
          //
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