package com.terminal.ui

import com.cmd3.app.ComponentWidget
import com.terminal.TerminalDisplay
import com.terminal.TtyConnector
import java.awt.Dimension

interface TerminalWidget : ComponentWidget {
    fun createTerminalSession(ttyConnector: TtyConnector?): JediTermWidget


    fun canOpenSession(): Boolean
    fun setTerminalPanelListener(terminalPanelListener: TerminalPanelListener)
    val preferredSize: Dimension
    val currentSession: TerminalSession
    val terminalDisplay: TerminalDisplay
    fun addListener(listener: TerminalWidgetListener)
    fun removeListener(listener: TerminalWidgetListener)
    fun grabFocus()
}