package com.cmd3.app

import com.cmd3.app.command.CommandProcessor
import com.cmd3.app.data.Command
import com.cmd3.app.data.H2
import com.cmd3.app.ux.CommandListPane
import com.cmd3.app.ux.MainMenuBar
import com.cmd3.app.ux.MainToolbar
import com.cmd3.app.ux.SelectionListener
import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JSplitPane


class Application : JFrame(), SelectionListener, KeyListener {

    private lateinit var mainMenuBar: MainMenuBar
    private lateinit var toolbar: MainToolbar
    private lateinit var terminalPanel : TerminalMain
    private lateinit var commandListPane : CommandListPane
    private lateinit var  splitPanel : JSplitPane
    private val commandProcessor = CommandProcessor(this)
    private val dao = H2()

    private fun go() {

        dao.connect()

        toolbar = MainToolbar(this)
        commandListPane = CommandListPane(this)
        terminalPanel = TerminalMain()
        terminalPanel.addCustomKeyListener(this)


        val scrollPane = JScrollPane(commandListPane)

        splitPanel = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, terminalPanel.terminal.component)

        this.layout = BorderLayout()
        this.title = "CommandCube"

        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.setLocationRelativeTo(null)
        mainMenuBar = MainMenuBar(this)

        jMenuBar = mainMenuBar


        add(toolbar, BorderLayout.PAGE_START)
        add(splitPanel, BorderLayout.CENTER)
        //add(splitPanel, B)

        EventQueue.invokeLater {
            this.isVisible = true
            terminalPanel.openSession()
        }
    }






    override fun commandSelected(command: Command, cr: Boolean) {
        println("Command Selected $command")
        if (Prefs.autoTerm) {
            terminalPanel.sendCommand(command, Prefs.autoCR || cr)
        }

    }

    override fun deleteSelectedCommands() {
        commandListPane.deleteSelectedCommands()
    }

    override fun enableMultiSelect(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun commandEntered() {
        if (Prefs.recording) {
            commandListPane.commandEntered()
        }
    }

    override fun clearConsole() {

        commandProcessor.clear()

        terminalPanel.clearConsole()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val jSplitPaneDemo1 = Application()

            jSplitPaneDemo1.defaultCloseOperation = EXIT_ON_CLOSE // Set Program to shutdown when frame closses
            jSplitPaneDemo1.setSize(1200, 600) // Set frame size
            jSplitPaneDemo1.isVisible = true // Display frame
            jSplitPaneDemo1.go()
        }
    }

    override fun keyTyped(k: KeyEvent) {
       commandProcessor.keyTyped(k)
    }

    override fun keyPressed(k: KeyEvent) {

    }

    override fun keyReleased(k: KeyEvent) {
        TODO("Not yet implemented")
    }
}