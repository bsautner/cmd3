package com.jediterm.terminal.command

import java.awt.event.KeyEvent


class CommandProcessor(private val commandListener: CommandListener)  {

    private val sb = StringBuilder()


    fun keyTyped(e: KeyEvent) {

        when(e.keyChar.toInt()) { //enter
            10 -> {

                commandListener.commandEntered(sb.toString())
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


}