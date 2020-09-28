package com.app.ux

import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem


class MainMenuBar(val selectionListener: SelectionListener) : JMenuBar() {


    init {
        val menu = JMenu("File")

        val exitMenuItem = JMenuItem("Exit")
        exitMenuItem.addChangeListener {
          
        }

        menu.add(exitMenuItem)


        add(menu)



    }

}