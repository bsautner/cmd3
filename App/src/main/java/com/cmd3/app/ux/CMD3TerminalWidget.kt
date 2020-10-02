package com.cmd3.app.ux

import com.cmd3.app.TerminalPanel
import com.intellij.ui.components.JBScrollBar
import com.intellij.ui.components.JBScrollPane
import com.terminal.TerminalStarter
import com.terminal.TtyBasedArrayDataStream
import com.terminal.TtyConnector
import com.terminal.model.Cmd3Terminal
import com.terminal.model.StyleState
import com.terminal.model.TerminalTextBuffer
import com.terminal.ui.CMD3TermWidget
import com.terminal.ui.settings.SettingsProvider
import javax.swing.JScrollBar

class CMD3TerminalWidget(settingsProvider: SettingsProvider?) : CMD3TermWidget(settingsProvider!!) {
    override fun createTerminalPanel(
        settingsProvider: SettingsProvider,
        styleState: StyleState,
        textBuffer: TerminalTextBuffer
    ): TerminalPanel {
        return TerminalPanel(settingsProvider, styleState, textBuffer)
    }

    override fun createTerminalStarter(terminal: Cmd3Terminal, connector: TtyConnector): TerminalStarter {
        return TerminalStarter(terminal, connector, TtyBasedArrayDataStream(connector))
    }

    override fun createScrollBar(): JScrollBar {
        val bar = JBScrollBar()
        bar.putClientProperty(JBScrollPane.Alignment::class.java, JBScrollPane.Alignment.RIGHT)

        return bar
    }

    init {
        name = "com/terminal"
    }
}