package com.terminal.command

import com.app.data.H2
import java.awt.event.KeyEvent


class CommandProcessor : CommandListener {

    private val sb = StringBuilder()
    private val dao = H2()




    override fun keyTyped(e: KeyEvent) {

        when(e.keyChar.toInt()) { //enter
            10 -> {

                commandEntered(sb.toString())
                sb.setLength(0)


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
    }

    override fun commandSelected(command: String) {
        sb.append(command)
    }

    companion object {
        val instance : CommandProcessor by lazy { CommandProcessor() }
    }


}