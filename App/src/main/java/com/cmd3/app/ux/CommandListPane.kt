package com.cmd3.app.ux

import com.cmd3.app.Prefs
import com.cmd3.app.data.Command
import com.cmd3.app.data.H2
import com.cmd3.app.command.CommandProcessor
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener


class CommandListPane(private val selectionListener: SelectionListener) : JPanel(), ListSelectionListener {
    private val list: JList<*>

    var dao : H2 = H2()
    val selected : MutableList<Command> = ArrayList()


    init {


        list = JList<Command>()


        list.model = defaultListModel()

        list.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        list.selectedIndex = 0
        list.addListSelectionListener(this)
        val listScrollPane = JScrollPane(list)
        listScrollPane.preferredSize = Dimension(400, 800)

        add(listScrollPane, BorderLayout.CENTER)

        list.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                selected.clear()
                selected.addAll(list.selectedValuesList)

                println(list.selectedIndices.size)
                if (e.isPopupTrigger) {
                    val menu = JPopupMenu()
                    val itemRemove = JMenuItem("Remove")
                    itemRemove.addActionListener { deleteSelectedCommands() }
                    val itemExecute = JMenuItem("Execute")
                    itemRemove.addActionListener { executeSelectedCommands() }
                    val itemScript = JMenuItem("Script")
                    itemRemove.addActionListener { scriptSelectedCommands() }
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
        if (!e.valueIsAdjusting && list.selectedIndex > -1 && Prefs.autoTerm) {
                CommandProcessor.instance.commandSelected((list.selectedValue as Command).cmd)
                selectionListener.commandSelected((list.selectedValue as Command))
                list.clearSelection()
        }
    }

    fun deleteSelectedCommands() {
        println("deleteSelectedCommands ${selected.size}")

        for (i in selected) {
            println("Deleting $i")
            dao.deleteCommand(i)
        }

        list.model = defaultListModel()



    }

    fun executeSelectedCommands() {


        for (i in selected) {
            selectionListener.commandSelected(i, true)
        }

        list.model = defaultListModel()
        list.clearSelection()

    }

    fun scriptSelectedCommands() {
        val selected =  list.selectedValuesList


    }

    fun commandEntered() {
        list.model = defaultListModel()
    }


}