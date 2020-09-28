package com.cmd3.app.ux

import com.cmd3.app.data.Command

interface SelectionListener {

    fun commandSelected(command: Command, cr: Boolean = false)
    fun deleteSelectedCommands()
    fun enableMultiSelect(enabled: Boolean)
    fun commandEntered()
    fun clearConsole()

}