import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.*
import kotlin.random.Random

fun main(args: Array<String>) {
    println("Hello World!")
    createGUI()
}

fun createGUI() {
    val mainWindow = JFrame("LettersTiming")
    mainWindow.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val northBox = Box(BoxLayout.Y_AXIS)
    val userBox = Box(BoxLayout.X_AXIS)
    val fioBox = Box(BoxLayout.Y_AXIS)
    val fioLabel = JLabel("FIO")
    val fioField = JTextField(30)
    fioField.setMaximumSize(Dimension(600, fioField.getMinimumSize().height))
    val newUserButton = JButton("New user")
    fioBox.add(fioLabel)
    fioBox.add(fioField)
    userBox.add(fioBox)
    userBox.add(Box.createHorizontalGlue())
    userBox.add(newUserButton)
    northBox.add(userBox)

    val lettersBox = Box(BoxLayout.X_AXIS)  //коробка с буквами
    val textField = JTextField(50)
    val sizeLabel = JLabel("text size: ")
    val fieldLength = JTextField(5)
    fieldLength.text = "10"
    val genButton = JButton("Generate text")
    var lettersCSV = """"""
    val stopSymbols = """|~[]{}';:\|/?`="""
    var symbol: Char
    var firstKey = true
    val textArea = JTextArea()
    genButton.addActionListener {
        firstKey = true
        lettersCSV = """"""
        var textString: String
        var symbols = CharArray(fieldLength.text.toInt())
        for (i in 0..fieldLength.text.toInt() - 1) {
            do {
                symbol = Random.nextInt(35, 126).toChar()
//                println(symbol)
            } while (stopSymbols.contains(symbol))
            print("$symbol")
            symbols[i] = symbol
        }
        textString = symbols.concatToString()
        println("textString = $textString")
        textField.text = textString
        lettersCSV = textString + ";\n"
        textArea.requestFocus()
    }

    lettersBox.border = BorderFactory.createLineBorder(Color.BLUE, 5)
    lettersBox.add(textField)
    lettersBox.add(sizeLabel)
    lettersBox.add(fieldLength)
    lettersBox.add(genButton)
    northBox.add(lettersBox)

    textArea.border = BorderFactory.createLineBorder(Color.GRAY, 5)
    var startTime: Long = 0
    var curString = ""
    textArea.addKeyListener(object : KeyAdapter() {
        override fun keyTyped(e: KeyEvent?) {
            super.keyTyped(e)
//            if (firstKey && e?.keyChar == textField.text[0])
            if (firstKey) {  //если первый символ
                if (e?.keyChar == textField.text[0]) { //и он правильный
                    firstKey = false
                    println("timer started")
                    curString = ""
                    curString += e?.keyChar
                    lettersCSV += e?.keyChar + ";\n"
                    startTime = System.currentTimeMillis()

                } else { //если первый символ не правильный
//                        val text = textArea.text
                    SwingUtilities.invokeLater(Runnable() {  //то не печатаем его
                        run() {
                            textArea.text = ""
                        }
                    })
                }
            } else {  //для остальных символов
                val curTime = System.currentTimeMillis()
                val timeForLetter = curTime - startTime
                println("time for ${e?.keyChar} = ${timeForLetter}")
                startTime = curTime
                lettersCSV += e!!.keyChar + ";$timeForLetter\n"
                curString += e?.keyChar
            }
            if (curString.equals(textField.text)) {
                lettersCSV +=";\n"
                println("lettersCSV = $lettersCSV")
                File("my1.csv").appendText(lettersCSV)
            }
        }
    })

    mainWindow.add(northBox, BorderLayout.NORTH)
    mainWindow.add(textArea, BorderLayout.CENTER)
    mainWindow.size = Dimension(800, 600)
    mainWindow.setLocationRelativeTo(null)
    mainWindow.isVisible = true
}