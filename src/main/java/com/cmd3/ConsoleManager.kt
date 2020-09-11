package com.cmd3

import javax.swing.JTextArea

class ConsoleManager(private val console : JTextArea) {
    
    
     fun prompt(enter: Boolean) {
        if (console.text.isBlank() || enter) {
            console.append("$ ")
        } else {
            console.append("\n$ ")
        }

        val len: Int = console.document.length
        console.caretPosition = len
        console.caret.isVisible = true
        console.grabFocus()

    }
}