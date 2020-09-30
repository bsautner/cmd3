package com.cmd3.app.ux

import com.cmd3.app.Prefs
import com.cmd3.app.command.CommandProcessor
import com.cmd3.app.data.Command
import com.cmd3.app.data.H2
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener


class CommandListPane(private val selectionListener: SelectionListener) : JList<Command>(), ListSelectionListener {

    var dao: H2 = H2()
    val selected: MutableList<Command> = ArrayList()


    init {

        this.model = defaultListModel()

        this.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        this.selectedIndex = 0
        this.addListSelectionListener(this)
//        val listScrollPane = JScrollPane(list)
       // this..layout = BorderLayout()
      //  listScrollPane.preferredSize = Dimension(400, 800)

        ///add(listScrollPane, BorderLayout.CENTER)
        val instance = this

        this.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                selected.clear()
                selected.addAll(selectedValuesList)

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
                    menu.show(instance, e.point.x, e.point.y)
                }
            }
        })
    }

    fun getComponent(): JComponent {
        return this
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
        if (!e.valueIsAdjusting && selectedIndex > -1 && Prefs.autoTerm) {

            selectionListener.commandSelected((selectedValue as Command))
            clearSelection()

        }
    }

    fun deleteSelectedCommands() {
        println("deleteSelectedCommands ${selected.size}")

        for (i in selected) {
            println("Deleting $i")
            dao.deleteCommand(i)
        }

        model = defaultListModel()


    }

    fun executeSelectedCommands() {


        for (i in selected) {
            selectionListener.commandSelected(i, true)
        }

        model = defaultListModel()
        clearSelection()

    }

    fun scriptSelectedCommands() {
        val selected = selectedValuesList


    }

    fun commandEntered() {
         model = defaultListModel()
    }


}