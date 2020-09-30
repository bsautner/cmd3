package com.cmd3.app


import com.cmd3.app.command.CommandProcessor
import com.cmd3.app.data.Command
import com.cmd3.app.data.H2
import com.cmd3.app.ux.*
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import java.awt.BorderLayout
import java.awt.Color
import java.awt.EventQueue
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
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

    private class App : SelectionListener, ActionListener, KeyListener {
        private val dao = H2()
        private lateinit var term: TerminalMain
        private lateinit var mainFrame: JFrame

        private lateinit var mainMenuBar: MainMenuBar
        private lateinit var commandMain: CommandMain
        val jSplitPane: JSplitPane = MainSplitPane()

        fun launch() {
            CommandProcessor.instance.selectionListener = this
            dao.connect()
            mainFrame = JFrame()
            mainMenuBar = MainMenuBar(this)

            mainFrame.jMenuBar = mainMenuBar

            mainFrame.setSize(1200, 800)
            mainFrame.layout = BorderLayout()
            mainFrame.title = "CommandCube"

            mainFrame.add(MainToolbar(this), BorderLayout.PAGE_START)


            term = TerminalMain()
            commandMain = CommandMain(this)

            jSplitPane.rightComponent = term.terminal.component
            jSplitPane.leftComponent = commandMain.listFrame

            jSplitPane.setDividerLocation(0.4)
            jSplitPane.leftComponent.background = Color.BLUE
            jSplitPane.rightComponent.background = Color.BLUE

            mainFrame.add(jSplitPane)

            mainFrame.pack()
            mainFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            mainFrame.contentPane.add(jSplitPane)
            //mainFrame.contentPane.add(term.myTerminal.component)
            //   mainFrame.pack()
            mainFrame.setLocationRelativeTo(null)
            EventQueue.invokeLater {
                mainFrame.isVisible = true
            }

            term.openSession()

            //
        }

        //        override fun commandEntered(command: String) {
//            println("Command Entered $command")
//            dao.insertCommand(command)
//        }
//
        override fun commandSelected(command: Command, cr: Boolean) {
            println("Command Selected $command")
            if (Prefs.autoTerm) {
                term.sendCommand(command, Prefs.autoCR || cr)
            }

        }

        override fun deleteSelectedCommands() {
            commandMain.listFrame.deleteSelectedCommands()
        }

        override fun enableMultiSelect(enabled: Boolean) {
            TODO("Not yet implemented")
        }

        override fun commandEntered() {
            if (Prefs.recording) {
                commandMain.listFrame.commandEntered()
            }
        }

        override fun clearConsole() {
            term = TerminalMain()
            CommandProcessor.instance.clear()
            jSplitPane.rightComponent = term.terminal.component
        }

        override fun actionPerformed(p0: ActionEvent?) {


        }

        override fun keyTyped(p0: KeyEvent?) {
            TODO("Not yet implemented")
        }

        override fun keyPressed(p0: KeyEvent?) {
            TODO("Not yet implemented")
        }

        override fun keyReleased(p0: KeyEvent?) {
            TODO("Not yet implemented")
        }

    }
}