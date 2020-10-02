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
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.ImageIcon
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
        initView()

        dao.connect()


        //add(splitPanel, B)

        EventQueue.invokeLater {

            this.isVisible = true
            terminalPanel.openSession()
        }
    }



    private fun initView() {
        this.title = "CommandCube"

        this.layout = BorderLayout()
        this.setLocationRelativeTo(null)
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.setSize(1200, 600) // Set frame size
        this.toolbar = MainToolbar(this)
        this.commandListPane = CommandListPane(this)
        this.terminalPanel = TerminalMain()
        this.terminalPanel.addCustomKeyListener(this)

        val scrollPane = JScrollPane(commandListPane)
        this.splitPanel = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, terminalPanel.terminal.component)
        this.mainMenuBar = MainMenuBar(this)
        this.jMenuBar = mainMenuBar
        this.iconImage = getIcon("icon").image
        this.



        add(toolbar, BorderLayout.PAGE_START)
        add(splitPanel, BorderLayout.CENTER)

    }

    private fun getIcon(icon: String): ImageIcon {
        val resource: URL = javaClass.classLoader.getResource("images/$icon.png")!!


        val file = File(resource.toURI())
        if (!file.exists()) {
            throw RuntimeException("$icon not found")
        }
        val image = ImageIO.read(file).getScaledInstance(MainToolbar.iconSize, MainToolbar.iconSize, 0)


        return ImageIcon(image)

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
            val app = Application()
            app.go()
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