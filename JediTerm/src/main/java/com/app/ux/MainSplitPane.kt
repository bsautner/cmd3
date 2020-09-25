package com.app.ux


import com.app.data.H2
import com.terminal.command.CommandProcessor
import java.awt.Dimension
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

class SplitPaneDemo(private val selectionListener: SelectionListener) : JPanel(), ListSelectionListener {
    private val picture: JPanel
    private val list: JList<*>
    val splitPane: JSplitPane
    var dao : H2 = H2()


    val commands = dao.getCommands()
    //Listens to the list
    override fun valueChanged(e: ListSelectionEvent) {
        if (!e.valueIsAdjusting) { //This line prevents double events
            CommandProcessor.instance.commandSelected(commands[list.selectedIndex].cmd)
            selectionListener.commandSelected(commands[list.selectedIndex].cmd)
        }
    }

    init {
        //Create the list of images and put it in a scroll pane.
        val commandArray = Array(commands.size) {
            commands[it].cmd
        }
        list = JList<String>(commandArray)
        list.selectionMode = ListSelectionModel.SINGLE_INTERVAL_SELECTION
        list.selectedIndex = 0
        list.addListSelectionListener(this)
        val listScrollPane = JScrollPane(list)
        picture = JPanel()
        // picture.setFont(picture.getFont().deriveFont(Font.ITALIC));
        // picture.setHorizontalAlignment(JLabel.CENTER);
        val pictureScrollPane = JScrollPane(picture)

        //Create a split pane with the two scroll panes in it.
        splitPane = JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            listScrollPane, pictureScrollPane
        )

        splitPane.isOneTouchExpandable = true
        splitPane.setDividerLocation(0.5)

        //Provide minimum sizes for the two components in the split pane.
        val minimumSize = Dimension(100, 50)
        listScrollPane.minimumSize = minimumSize
        pictureScrollPane.minimumSize = minimumSize

        //Provide a preferred size for the split pane.
        splitPane.preferredSize = Dimension(1200, 800)
    }
}