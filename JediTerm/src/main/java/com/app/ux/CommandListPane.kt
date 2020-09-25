package com.app.ux

import com.app.data.Command
import com.app.data.H2
import com.terminal.command.CommandProcessor
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener


class CommandListPane(private val selectionListener: SelectionListener) : JPanel(), ListSelectionListener {
    private val list: JList<*>

    var dao : H2 = H2()



    init {


        list = JList<Command>()

        list.model = defaultListModel()

        list.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        list.selectedIndex = 0
        list.addListSelectionListener(this)
        val listScrollPane = JScrollPane(list)


        listScrollPane.minimumSize = minimumSize
        listScrollPane.preferredSize = Dimension(350, 600)

        add(listScrollPane)

        list.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    val menu = JPopupMenu()
                    val itemRemove = JMenuItem("Remove")
                    itemRemove.addActionListener { deleteSelectedCommands()  }
                    val itemExecute = JMenuItem("Execute")
                    itemRemove.addActionListener { executeSelectedCommands()  }
                    val itemScript = JMenuItem("Script")
                    itemRemove.addActionListener { scriptSelectedCommands()  }
                    menu.add(itemRemove)
                    menu.add(itemExecute)
                    menu.add(itemScript)
                    menu.show(list, e.point.x, e.point.y)
                }
            }
        })
    }

    private fun defaultListModel(): DefaultListModel<Command> {
        val commands = dao.getCommands()
        val listModel = DefaultListModel<Command>()
        for ((index, value) in commands.withIndex()) {
            listModel.add(index, value)
        }
        return listModel
    }

    //Listens to the list
    override fun valueChanged(e: ListSelectionEvent) {
        if (!e.valueIsAdjusting && list.selectedIndex > -1) { //This line prevents double events
            CommandProcessor.instance.commandSelected((list.selectedValue as Command).cmd)
            selectionListener.commandSelected((list.selectedValue as Command))
        }
    }

    fun deleteSelectedCommands() {
        val selected =  list.selectedValuesList

        for (i in selected) {
            dao.deleteCommand(i as Command)
        }

        list.model = defaultListModel()



    }

    fun executeSelectedCommands() {
        val selected =  list.selectedValuesList

        for (i in selected) {
            selectionListener.commandSelected(i as Command, true)
        }

        list.model = defaultListModel()

    }

    fun scriptSelectedCommands() {
        val selected =  list.selectedValuesList

        for (i in selected) {
            dao.deleteCommand(i as Command)
        }

        list.model = defaultListModel()

    }

    fun commandEntered() {
        list.model = defaultListModel()
    }
}