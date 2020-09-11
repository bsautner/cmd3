import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.*
import javax.swing.border.EtchedBorder
import javax.swing.text.*


object Application  : JFrame(), KeyListener {

    private val home: String = System.getProperty("user.home")
    private val user: String = System.getProperty("user.name")
    const val ANSI_RESET = "\u001B[0m"
    const val ANSI_BLACK = "\u001B[30m"
    const val ANSI_RED = "\u001B[31m"
    const val ANSI_GREEN = "\u001B[32m"
    const val ANSI_YELLOW = "\u001B[33m"
    const val ANSI_BLUE = "\u001B[34m"
    const val ANSI_PURPLE = "\u001B[35m"
    const val ANSI_CYAN = "\u001B[36m"
    const val ANSI_WHITE = "\u001B[37m"

    val sb = StringBuilder()
    val mainText = JTextArea()

    init {



        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1200, 800)
        setLocationRelativeTo(null)
        val mainPanel = JPanel()
        mainPanel.setBorder(EtchedBorder())

        val mainFrame = JFrame()
        mainFrame.layout = BorderLayout()
        mainFrame.add(mainPanel, BorderLayout.CENTER)
        mainFrame.pack()
        mainFrame.isVisible = true

       // mainPanel.setSize(1200, 800)

        mainText.addKeyListener(this)
        mainText.isFocusable = true
        mainText.autoscrolls = true
        mainText.background = Color.BLACK
        val font = Font("Default", Font.BOLD, 18)
        mainText.font = font
        mainText.foreground = Color.BLUE



        val caret = mainText.caret as DefaultCaret
        caret.updatePolicy = DefaultCaret.OUT_BOTTOM
        val scrollPane = JScrollPane(mainText)

       // mainPanel.add(scrollPane)
        add(scrollPane)

    }

    @JvmStatic
    fun main(vararg s: String) {

       resume()


    }

    private var dir = File(getPath())
    private fun resume() {
        printPath()
        isFocusable = true
        focusTraversalKeysEnabled = false
        isVisible = true
        o("System Ready \n")
        prompt()

    }

fun processCommand(input : String) {



            printPath()


            when {
                input.startsWith("cd ") -> {
                    navigate(input)
                }
                input.startsWith("sudo ") -> {
                    print("[sudo] password for $user: ")
                    val pass = readLine()
                    val cmd = arrayOf("/bin/bash", "-c", "echo $pass| $input")
                    runCommand(cmd)
                }
                else -> {
                    val bash = arrayOf("/bin/bash", "-c")
                     runCommand(bash.plus(input.split(" ").toTypedArray()))
                }
            }
            prompt()

    }
    private fun o(output: String?) {
        mainText.append("$output\n")
        val len: Int = mainText.document.length
        mainText.caretPosition = len


    }
    private fun prompt() {
        mainText.append("$ ")
        val len: Int = mainText.document.length
        mainText.caretPosition = len
        mainText.caret.isVisible = true
        mainText.grabFocus()

    }

    private fun appendToPane(msg: String, c: Color) {
        val sc = StyleContext.getDefaultStyleContext()
        var aset: AttributeSet? = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c)
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console")
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED)
        val len = mainText.document.length
        mainText.caretPosition = len

        mainText.replaceSelection(msg)
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
        title = dir.absolutePath

    }
    enum class navType {
        abs, rel
    }

    fun printPath() {
        title = dir.absolutePath.replace(home, "~")
    }

    fun getPath() : String {
        val currentRelativePath = Paths.get("")
        val s = currentRelativePath.toAbsolutePath().toString()

        return s
    }


    fun runCommand(commands: Array<String>) {
        try {
            val rt = Runtime.getRuntime()


            val proc = rt.exec(commands, emptyArray(), dir)

            val stdInput = BufferedReader(InputStreamReader(proc.inputStream))

            val stdError = BufferedReader(InputStreamReader(proc.errorStream))

          //  println("Here is the standard output of the command:\n")
            var s: String? = null
            while (stdInput.readLine().also { s = it } != null) {
                o(s)
            }


// Read any errors from the attempted command

// Read any errors from the attempted command
          //  println("Here is the standard error of the command (if any):\n")
            while (stdError.readLine().also { s = it } != null) {
                o(s)
            }
        } catch (ex: Exception) {
            o(ex.message)
        }
    }
    fun f() {

    }

    override fun keyTyped(k: KeyEvent?) {
        println(k?.keyChar?.toInt())
        when(k?.keyChar?.toInt()) {
            10 -> { //enter
               processCommand(sb.toString())
                sb.clear()
            }
            8 -> { //backspace
                if (sb.isNotEmpty()) {
                    sb.removeRange(sb.length - 1, sb.length)
                }
                else {
                    k.consume()
                    //prompt()
                }
            }
            9 -> { //tab

                if (sb.startsWith("cd ")) {
                    k.consume()
                    val original = sb.toString()
                    val find = sb.substring(sb.lastIndexOf('/') + 1)

                    val current = original.removePrefix("cd ").removeRange(sb.lastIndexOf('/')-3, sb.length-3)

                    println("suggesting dirs for $find in $current")
                    val f = File(current)

                    //TODO more tabs go to next option
                    if (f.exists()) {
                        for (d in Files.list(f.toPath())) {
                            println("found ${d.fileName}")
                            if (d.fileName.toString().startsWith(find)) {
                                o(d.fileName.toString().removePrefix(find))

                                println("matched ${d.fileName}")
                            }

                        }
                    }


                }
            }
            else -> {
                sb.append(k?.keyChar)
            }
        }
        println(sb.toString())


    }

    override fun keyPressed(p0: KeyEvent?) {
       // println("keyPressed")
    }

    override fun keyReleased(p0: KeyEvent?) {
       // println("keyReleased")
    }


}