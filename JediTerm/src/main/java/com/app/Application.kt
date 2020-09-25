package com.app

import com.app.data.Command
import com.app.data.H2
import com.app.ux.CommandListPane
import com.app.ux.MainMenuBar
import com.app.ux.MainSplitPane
import com.app.ux.SelectionListener
import com.terminal.command.CommandProcessor
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
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

    private class App : SelectionListener, ActionListener {
        private val dao = H2()
        private lateinit var term : JediTermMain
        private lateinit var mainFrame : JFrame
        private lateinit var listFrame : CommandListPane
        private lateinit var mainMenuBar : MainMenuBar

        fun launch() {
            CommandProcessor.instance.selectionListener = this
            dao.connect()
            mainFrame = JFrame()
            mainMenuBar = MainMenuBar(this)

            mainFrame.jMenuBar = mainMenuBar
            mainFrame.setSize(1200, 800)
            mainFrame.layout = BorderLayout()
            mainFrame.title = "CommandCube"
            listFrame = CommandListPane(this)

            term = JediTermMain()

            val jSplitPane : JSplitPane = MainSplitPane()
            jSplitPane.rightComponent = term.terminal.component
            jSplitPane.leftComponent = listFrame
            jSplitPane.setDividerLocation(0.4)

          //  mainFrame.add(jSplitPane)
          //  mainFrame.pack()
            mainFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            mainFrame.contentPane.add(jSplitPane)
            //mainFrame.contentPane.add(term.myTerminal.component)
         //   mainFrame.pack()
            mainFrame.setLocationRelativeTo(null)
            EventQueue.invokeLater {
                mainFrame.isVisible = true
            }


          //
        }

//        override fun commandEntered(command: String) {
//            println("Command Entered $command")
//            dao.insertCommand(command)
//        }
//
        override fun commandSelected(command: Command, cr: Boolean) {
            println("Command Selected $command")
            if (mainMenuBar.termEnabled) {
                term.sendCommand(command, mainMenuBar.autoCr || cr)
            }

        }

        override fun deleteSelectedCommands() {
            listFrame.deleteSelectedCommands()
        }

        override fun enableMultiSelect(enabled: Boolean) {
            TODO("Not yet implemented")
        }

        override fun commandEntered() {
            listFrame.commandEntered()
        }

        override fun actionPerformed(p0: ActionEvent?) {
            println("action")
        }

    }
}