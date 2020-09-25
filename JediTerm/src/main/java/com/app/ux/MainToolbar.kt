package com.app.ux

import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JToolBar


class MainToolbar(private val selectionListener: SelectionListener) : JToolBar(), ActionListener {

        init {
            preferredSize = Dimension(20, 100)
            val button = makeNavigationButton(
                "clear-icon-9216", "clear",
                "Back to previous something-or-other",
                "Previous"
            )
             add(button)



        }

    protected fun makeNavigationButton(
        imageName: String,
        actionCommand: String?,
        toolTipText: String?,
        altText: String?
    ): JButton? {
        //Look for the image.

        //Create and initialize the button.
        val button = JButton("Clear Console")
        button.actionCommand = actionCommand
        button.toolTipText = toolTipText
        button.preferredSize = Dimension(20, 100)
        button.addActionListener(this)
                       //image found
        button.icon = ImageIcon("images/clear-icon-9216.png", altText)


        return button
    }

    override fun actionPerformed(p0: ActionEvent?) {
        selectionListener.clearConsole()
    }

}