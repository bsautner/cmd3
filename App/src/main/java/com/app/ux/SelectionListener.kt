package com.app.ux

import com.app.data.Command

interface SelectionListener {

    fun commandSelected(command: Command, cr: Boolean = false)
    fun deleteSelectedCommands()
    fun enableMultiSelect(enabled: Boolean)
    fun commandEntered()
    fun clearConsole()

}