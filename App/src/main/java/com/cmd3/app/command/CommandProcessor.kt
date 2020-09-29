package com.cmd3.app.command

import com.cmd3.app.data.H2
import com.cmd3.app.ux.SelectionListener
import java.awt.event.KeyEvent


class CommandProcessor : CommandListener {

    private val sb = StringBuilder()
    private val dao = H2()
    lateinit var selectionListener: SelectionListener




    override fun keyTyped(e: KeyEvent) {

        when(e.keyChar.toInt()) { //enter
            10 -> {

                commandEntered(sb.toString())
                clear()


            }

            8 -> { //DEL
                if (sb.isNotEmpty()) {
                    sb.deleteCharAt(sb.length - 1)
                }
            }

            else -> {
                sb.append(e.keyChar)
            }

        }

    }

    override fun commandEntered(command: String) {
        dao.insertCommand(command)
        selectionListener.commandEntered()
    }

    override fun commandSelected(command: String) {
        sb.append(command)
    }

    fun clear() {
        sb.setLength(0)
    }

    companion object {
        val instance : CommandProcessor by lazy { CommandProcessor() }
    }


}