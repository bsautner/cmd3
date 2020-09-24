package com.terminal.command

import java.awt.event.KeyEvent

interface CommandListener {

    fun commandEntered(command: String)
    fun commandSelected(command: String)
    fun keyTyped(e: KeyEvent)
}