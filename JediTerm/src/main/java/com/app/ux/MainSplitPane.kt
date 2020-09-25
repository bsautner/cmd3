package com.app.ux


import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.JSplitPane

class MainSplitPane() : JPanel() {

    val splitPane: JSplitPane = JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT
    )


    init {


        //Create a split pane with the two scroll panes in it.

        splitPane.isOneTouchExpandable = true


        //Provide a preferred size for the split pane.
        splitPane.preferredSize = Dimension(1200, 800)
    }
}