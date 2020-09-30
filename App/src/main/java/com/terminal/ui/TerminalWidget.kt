package com.terminal.ui


import com.terminal.TerminalDisplay
import com.terminal.TtyConnector
import java.awt.Dimension
import javax.swing.JComponent

interface TerminalWidget  {
    fun createTerminalSession(ttyConnector: TtyConnector?): CMD3TermWidget
    val component: JComponent

    fun canOpenSession(): Boolean
    fun setTerminalPanelListener(terminalPanelListener: TerminalPanelListener)
    val preferredSize: Dimension
    val currentSession: TerminalSession
    val terminalDisplay: TerminalDisplay
    fun addListener(listener: TerminalWidgetListener)
    fun removeListener(listener: TerminalWidgetListener)
    fun grabFocus()
}