package com.cmd3

import kotlinx.coroutines.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.coroutines.CoroutineContext

@ExperimentalStdlibApi
class CommandProcessor(private val consoleCallback: ConsoleCallback) : KeyListener {

    private var asking = false
    private var sudo = ""


    private val sb = StringBuilder()
    private val askingResponse = StringBuilder()

    private var dir: File = File(path)
    private val path : String
        get() {
            val currentRelativePath = Paths.get("")
            return currentRelativePath.toAbsolutePath().toString()
        }


    init {
        consoleCallback.setTitle(path)
    }

    override fun keyTyped(k: KeyEvent) {


        when (k.keyChar.toInt()) {
            10 -> { //enter
                println(asking)
                if (asking) {
                    asking = false
                } else {
                    GlobalScope.launch(Dispatchers.Default) {
                        processCommand(sb.toString())
                        sb.clear()
                    }


                }
            }
            8 -> { //backspace
                if (asking) {
                    if (askingResponse.isNotEmpty()) {
                        askingResponse.deleteAt(sb.length - 1)
                    }
                }
                else if (sb.isNotEmpty()) {
                    sb.deleteAt(sb.length - 1)
                } else {

                    consoleCallback.prompt(false)

                }
            }
            9 -> { //tab

                if (sb.startsWith("cd ")) {


                    val find = sb.toString().removePrefix("cd ")

                    println("suggesting dirs for $find in ${dir.absolutePath}")

                    //TODO more tabs go to next option
                    if (dir.exists()) {
                        for (d in Files.list(dir.toPath())) {

                            if (d.fileName.toString().startsWith(find)) {
                                consoleCallback.print(d.fileName.toString().removePrefix(find))

                                println("matched ${d.fileName}")
                            }

                        }
                    }


                }
            }
            else -> {
                if (asking) {
                    askingResponse.append(k.keyChar)
                } else {
                    sb.append(k.keyChar)
                }
            }
        }
        println(sb.toString())
    }


    private suspend fun processCommand(input: String) {

        printPath()


        when {
            input.isBlank() -> {
                consoleCallback.prompt(true)
            }
            input.startsWith("cd ") -> {
                navigate(input)
            }
            input.startsWith("sudo ") -> {
                if (sudo.isBlank()) {
                    asking = true
                    consoleCallback.print(SUDO_PROMPT)

                    while (asking) {
                        delay(10)
                    }
                    sudo = askingResponse.toString()
                }
                askingResponse.clear()


                val cmd = arrayOf("/bin/bash", "-c", "echo '$sudo' | ${input.replace("sudo", "sudo -k -S")}")


                for (s in cmd) {
                    println(s + " ")
                }
                runCommand(cmd)
            }
            else -> {
                val bash = arrayOf("/bin/bash", "-c")
                runCommand(bash.plus(input.split(" ").toTypedArray()))
            }
        }
        consoleCallback.prompt(true)

    }

    private fun printPath() {
        val title = dir.absolutePath.replace(home, "~")
        consoleCallback.setTitle(title)
    }

    fun navigate(command: String) {
        val s = command.replace("~", home).split(" ")[1]
        when {
            s.startsWith("/") -> {
                dir = File(s)
            }
            s.startsWith("./") -> {
                dir = File(dir.absolutePath + s.trimStart('.'))
            }
            File(dir.absolutePath + "/" + s).exists() -> {
                dir = File(dir.absolutePath + "/" + s)
            }
        }
        consoleCallback.setTitle(dir.absolutePath)

    }

    fun output(output: String?) {
        if (! skipOutput(output)) {
            consoleCallback.print("$output\n")
        }
//
//        val len: Int = mainText.document.length
//        mainText.caretPosition = len
    }

    override fun keyPressed(p0: KeyEvent?) {}

    override fun keyReleased(p0: KeyEvent?) {}

    fun runCommand(commands: Array<String>) {
        try {
            val rt = Runtime.getRuntime()


            val proc = rt.exec(commands, emptyArray(), dir)

            val stdInput = BufferedReader(InputStreamReader(proc.inputStream))

            val stdError = BufferedReader(InputStreamReader(proc.errorStream))

            var s: String?
            while (stdInput.readLine().also { s = it } != null) {
                output(s)
            }

            while (stdError.readLine().also { s = it } != null) {
                output("ERROR: $s")
            }
        } catch (ex: Exception) {
            output(ex.message)
        }
    }

    private fun skipOutput(s: String?) : Boolean {
        return SUDO_PROMPT.trim() == s?.trim()
    }

    companion object {
        private val home: String = System.getProperty("user.home")
        private val user: String = System.getProperty("user.name")
        val SUDO_PROMPT = "[me] password for ${user}: "

    }

}