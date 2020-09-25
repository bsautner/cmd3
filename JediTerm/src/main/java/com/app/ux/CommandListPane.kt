package com.app.ux

import com.app.data.H2
import com.terminal.command.CommandProcessor
import java.awt.Dimension
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

class CommandListPane(private val selectionListener: SelectionListener) : JPanel(), ListSelectionListener {
    private val list: JList<*>

    var dao : H2 = H2()

    val commands = dao.getCommands()
    init {

        val commandArray = Array(commands.size) {
            commands[it].cmd
        }
        list = JList<String>(commandArray)
        list.selectionMode = ListSelectionModel.SINGLE_INTERVAL_SELECTION
        list.selectedIndex = 0
        list.addListSelectionListener(this)
        val listScrollPane = JScrollPane(list)
        listScrollPane.minimumSize = minimumSize
        listScrollPane.preferredSize = Dimension(350, 800)
        add(listScrollPane)
    }

    //Listens to the list
    override fun valueChanged(e: ListSelectionEvent) {
        if (!e.valueIsAdjusting) { //This line prevents double events
            CommandProcessor.instance.commandSelected(commands[list.selectedIndex].cmd)
            selectionListener.commandSelected(commands[list.selectedIndex].cmd)
        }
    }
}