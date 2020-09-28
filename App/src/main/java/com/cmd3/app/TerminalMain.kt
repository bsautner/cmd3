package com.cmd3.app

import com.cmd3.app.ux.JediTerminalWidget
import com.cmd3.app.ux.TabbedTerminalWidget
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.intellij.execution.filters.UrlFilter
import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Pair
import com.intellij.util.EncodingEnvironmentUtil
import com.pty4j.PtyProcess
import com.terminal.LoggingTtyConnector
import com.terminal.TtyConnector
import com.terminal.ui.AbstractTerminalFrame
import com.terminal.ui.TerminalWidget
import com.terminal.ui.UIUtil
import com.terminal.ui.settings.DefaultTabbedSettingsProvider
import com.terminal.ui.settings.TabbedSettingsProvider
import com.terminal.util.PtyProcessTtyConnector
import java.io.IOException
import java.nio.charset.Charset
import java.util.function.Function

class TerminalMain : AbstractTerminalFrame(), Disposable {


    override fun dispose() {
        // TODO
    }

    override fun createTabbedTerminalWidget(): TabbedTerminalWidget {
        return object : TabbedTerminalWidget(
            DefaultTabbedSettingsProvider(),
            Function<Pair<TerminalWidget, String>, JediTerminalWidget> { pair -> openSession(pair.first) as JediTerminalWidget },
            this
        ) {
            override fun createInnerTerminalWidget(): JediTerminalWidget {
                return createTerminalWidget(settingsProvider)
            }
        }
    }

    override fun createTtyConnector(): TtyConnector {
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

    override fun createTerminalWidget(settingsProvider: TabbedSettingsProvider): JediTerminalWidget {
        val widget = JediTerminalWidget(settingsProvider, this)
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

        @Throws(IOException::class)
        override fun write(bytes: ByteArray) {

            super.write(bytes)
        }
    }

}