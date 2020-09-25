package com.app.ux

import javax.swing.*


class MainMenuBar(val selectionListener: SelectionListener) : JMenuBar() {
    val autoReturnCheckbox = JCheckBoxMenuItem("auto-cr")
    val autoCommandCheckbox = JCheckBoxMenuItem("click to terminal")

    val autoCr : Boolean
        get() =  autoReturnCheckbox.state
    val termEnabled : Boolean
        get() = autoCommandCheckbox.state

    init {
        val menu = JMenu("File")

        val deleteCommandMenuItem = JMenuItem("Delete Command")
        deleteCommandMenuItem.addActionListener {
            selectionListener.deleteSelectedCommands()
        }

        val m2 = JMenuItem("xxMenuItem2")
        val m3 = JMenuItem("xxMenuItem3")

        // add menu items to menu

        // add menu items to menu


        menu.add(deleteCommandMenuItem)
        menu.add(m2)
        menu.add(m3)


        add(menu)

        add(autoReturnCheckbox)
        autoCommandCheckbox.state = true
        add(autoCommandCheckbox)



    }

}