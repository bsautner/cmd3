package com.jediterm.terminal.command

interface CommandListener {

    fun commandEntered(command: String)
    fun commandSelected(command: String)
}