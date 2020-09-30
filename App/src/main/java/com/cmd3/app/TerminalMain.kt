package com.cmd3.app

import com.cmd3.app.data.Command
import com.cmd3.app.ux.CMD3TerminalWidget
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.intellij.execution.filters.UrlFilter
import com.intellij.util.EncodingEnvironmentUtil
import com.pty4j.PtyProcess
import com.terminal.LoggingTtyConnector
import com.terminal.TtyConnector
import com.terminal.ui.CMD3TermWidget
import com.terminal.ui.PreConnectHandler
import com.terminal.ui.TerminalWidget
import com.terminal.ui.UIUtil
import com.terminal.ui.settings.DefaultSettingsProvider
import com.terminal.ui.settings.SettingsProvider
import com.terminal.util.PtyProcessTtyConnector
import java.io.IOException
import java.nio.charset.Charset

class TerminalMain {


    var terminal: TerminalWidget = createTerminalWidget()


    fun createTerminalWidget(): CMD3TerminalWidget {
        return createTerminalWidget(DefaultSettingsProvider())

    }


    fun openSession() {
        openSession(terminal)
    }

    fun createTtyConnector(): TtyConnector {
        try {

            val charset = Charset.forName("UTF-8")

            val envs = Maps.newHashMap(System.getenv())

            EncodingEnvironmentUtil.setLocaleEnvironmentIfMac(envs, charset)

            val command: Array<String>

            if (UIUtil.isWindows) {
                command = arrayOf("cmd.exe")
            } else {
                command = arrayOf("/bin/bash", "--login")
                envs["TERM"] = "xterm"
            }

            val process = PtyProcess.exec(command, envs, null)


            return LoggingPtyProcessTtyConnector(process, charset)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }

    }

    fun createTerminalWidget(settingsProvider: SettingsProvider): CMD3TerminalWidget {
        val widget = CMD3TerminalWidget(settingsProvider)
        widget.addHyperlinkFilter(UrlFilter())
        return widget

    }

    class LoggingPtyProcessTtyConnector(process: PtyProcess, charset: Charset) :
        PtyProcessTtyConnector(process, charset), LoggingTtyConnector {
        private val myDataChunks = Lists.newArrayList<CharArray>()

        @Throws(IOException::class)
        override fun read(buf: CharArray, offset: Int, length: Int): Int {
            val len = super.read(buf, offset, length)
            if (len > 0) {
                val arr = buf.copyOfRange(offset, len)
                myDataChunks.add(arr)
            }
            return len
        }

        override fun getChunks(): List<CharArray> {
            return Lists.newArrayList(myDataChunks)
        }

        @Throws(IOException::class)
        override fun write(string: String) {

            super.write(string)
        }

        override fun init(myPreConnectHandler: PreConnectHandler?): Boolean {
            return true
          //  TODO("Not yet implemented")
        }

        @Throws(IOException::class)
        override fun write(bytes: ByteArray) {

            super.write(bytes)
        }
    }

    private fun openSession(terminal: TerminalWidget): CMD3TermWidget? {
        return if (terminal.canOpenSession()) {
            val session = terminal.createTerminalSession(createTtyConnector())
            session.start()
            return session
        } else null
    }

    fun sendCommand(command: Command, autoCr: Boolean) {
        try {
            terminal.currentSession.ttyConnector.write(command.cmd)
            if (autoCr) {
                terminal.currentSession.ttyConnector.write("\n")
            }
            terminal.grabFocus()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun clearConsole() {
        sendCommand(Command("clear"), true)
    }


}