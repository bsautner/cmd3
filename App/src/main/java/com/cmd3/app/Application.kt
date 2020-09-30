package com.cmd3.app

import com.cmd3.app.command.CommandProcessor
import com.cmd3.app.data.Command
import com.cmd3.app.ux.CommandListPane
import com.cmd3.app.ux.MainMenuBar
import com.cmd3.app.ux.MainToolbar
import com.cmd3.app.ux.SelectionListener
import java.awt.BorderLayout
import java.awt.Color
import java.awt.EventQueue
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JSplitPane


class Application : JFrame(), SelectionListener{

    private lateinit var mainMenuBar: MainMenuBar
    private lateinit var toolbar: MainToolbar
    private lateinit var terminalPanel : TerminalMain
    private lateinit var commandListPane : CommandListPane
    private lateinit var  splitPanel : JSplitPane
    private fun go() {
        CommandProcessor.instance.selectionListener = this

        toolbar = MainToolbar(this)
        commandListPane = CommandListPane(this)
        terminalPanel = TerminalMain()

        val scrollPane = JScrollPane(commandListPane)
        val splitPanel = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, terminalPanel.terminal.component)
        splitPanel.rightComponent.background= Color.BLUE
        splitPanel.leftComponent.background= Color.ORANGE

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

        CommandProcessor.instance.clear()

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
}