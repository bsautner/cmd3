package com.app.ux


import java.awt.Dimension
import javax.swing.JSplitPane

class MainSplitPane : JSplitPane(HORIZONTAL_SPLIT) {


    init {


        //Create a split pane with the two scroll panes in it.

         isOneTouchExpandable = true


        //Provide a preferred size for the split pane.
        preferredSize = Dimension(1200, 800)
    }
}