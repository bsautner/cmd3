package com.cmd3.app.ux

import com.cmd3.app.Prefs
import java.awt.event.ActionListener
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JToggleButton
import javax.swing.JToolBar


class MainToolbar(selectionListener: SelectionListener) : JToolBar() {

    init {
        //preferredSize = Dimension(20, 100)
        // val clearButton = makeNavigationButton(ActionListener { selectionListener.clearConsole() }, "276-trash")

        val clearButton = JButton("Clear Console")
        clearButton.addActionListener {
            selectionListener.clearConsole()
        }

        add(clearButton)
        add(makeToggle(ActionListener {
            Prefs.autoCR = !Prefs.autoCR
        }, "Send CR", Prefs.autoCR))

        add(makeToggle(ActionListener {
            Prefs.recording = !Prefs.recording
        }, "Record Commands", Prefs.recording))

        add(makeToggle(ActionListener {
            Prefs.autoTerm = !Prefs.autoTerm
        }, "Send Commands", Prefs.autoTerm))


    }

    private fun getRecordIcon(): String {
        return if (Prefs.recording) {
            pauseIcon
        } else {
            recordIcon
        }
    }

    fun makeToggle(actionListener: ActionListener, text: String, state: Boolean): JToggleButton {
        val t = JToggleButton(text, state)
        t.addActionListener(actionListener)
        return t
    }

    private fun makeNavigationButton(actionListener: ActionListener, icon: String): JButton {


        val button = JButton(getIcon(icon))
        button.isBorderPainted = false
        button.isFocusPainted = false
        button.isContentAreaFilled = false
        button.addActionListener(actionListener)

        return button
    }

    private fun getIcon(icon: String): ImageIcon {
        val resource: URL = javaClass.classLoader.getResource("icons/png/$icon.png")!!


        val file = File(resource.toURI())
        if (!file.exists()) {
            throw RuntimeException("$icon not found")
        }
        val image = ImageIO.read(file).getScaledInstance(iconSize, iconSize, 0)


        return ImageIcon(image)

    }

    companion object {
        val iconSize = 40
        private const val pauseIcon = "038-pause"
        private const val recordIcon = "010-record"
    }


}