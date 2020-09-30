package com.cmd3.app.ux

import javax.swing.JPanel

class CommandMain(private val selectionListener: SelectionListener) : JPanel() {

    public val listFrame: CommandListPane = createList()



    fun createList() : CommandListPane{
        return CommandListPane(selectionListener)
    }


}