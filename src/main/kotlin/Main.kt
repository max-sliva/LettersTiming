import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.File
import java.util.*
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
    fioField.text = "testUser"
    fioField.maximumSize = Dimension(600, fioField.minimumSize.height)
    val newUserButton = JButton("New user")
    val group = ButtonGroup()  //группа для радиокнопок
    val radioBut1 = JRadioButton("Typed")  //режим замера времени только между нажатиями клавиш
    val radioBut2 = JRadioButton("Press / Release")  //режим замера сколько была нажата клавиша
    radioBut2.isSelected = true //по умолчанию второй режим
    group.add(radioBut1)
    group.add(radioBut2)
    val checkBoth = JCheckBox("Both")  //замер в обоих режимах
    fioBox.add(fioLabel)
    fioBox.add(fioField)
    userBox.add(fioBox)
    userBox.add(Box.createHorizontalGlue())
    userBox.add(newUserButton)
    userBox.add(radioBut1)
    userBox.add(radioBut2)
    userBox.add(checkBoth)
    northBox.add(userBox)
    checkBoth.isSelected = true // по умолчанию вкл замер в обоих режимах
    for (element in group.elements) {  //делаем неактивными радиокнопки
        element.isEnabled = !checkBoth.isSelected
    }
    checkBoth.addActionListener {           //переключаем активность радиокнопок в зависимости от чекбокса checkBoth
        for (element in group.elements) {
            element.isEnabled = !checkBoth.isSelected
        }
    }
    val textArea = JTextArea()
    var firstKey = true  //признак нажатия первого символа

    val lettersBox = Box(BoxLayout.X_AXIS)  //коробка с буквами
    val textField = JTextField(50)
    textField.font = Font("Tahoma", Font.PLAIN, 16)
    textField.addKeyListener(object : KeyAdapter() {
        override fun keyTyped(e: KeyEvent?) {
            textArea.isEnabled = true
            firstKey = true
        }
    })

    val sizeLabel = JLabel("text size: ")
    val stringLength = JTextField(5)
    stringLength.text = "5"
    val genButton = JButton("Generate text")
    var lettersCSV = """"""  //строка для записи в csv файл
    val stopSymbols = """|~[]{}';:\|/?`="""  //символы, которые исключаются из генерации
    var symbol: Char
    textArea.font = Font("Tahoma", Font.PLAIN, 16)
    textArea.isEnabled = false
    var curString = ""
    genButton.addActionListener {
        firstKey = true
        lettersCSV = """"""
        var textString: String
        var symbols = CharArray(stringLength.text.toInt())
        for (i in 0..stringLength.text.toInt() - 1) {
            do {
                symbol = Random.nextInt(35, 126).toChar()
//                println(symbol)
            } while (stopSymbols.contains(symbol))
            print("$symbol")
            symbols[i] = symbol
        }
        textString = symbols.concatToString()
        println("\ntextString = $textString")
        textField.text = textString
        lettersCSV = textString + ";\n"
        textArea.requestFocus()
        curString = ""
        textArea.isEnabled = true
    }

    lettersBox.border = BorderFactory.createLineBorder(Color.BLUE, 5)
    lettersBox.add(textField)
    lettersBox.add(sizeLabel)
    lettersBox.add(stringLength)
    lettersBox.add(genButton)
    northBox.add(lettersBox)

    textArea.border = BorderFactory.createLineBorder(Color.GRAY, 5)
    var startTime: Long = 0
    var pressTime: Long = 0
    var releaseTime: Long = 0
    textArea.addKeyListener(object : KeyAdapter() {
        override fun keyTyped(e: KeyEvent?) {
            super.keyTyped(e)
            if (radioBut1.isSelected || checkBoth.isSelected) { //если выбран первый режим или оба режима
                if (firstKey) {  //если первый символ
                    if (e?.keyChar == textField.text[0]) { //и он правильный
                        firstKey = false
                        println("timer started")
                        curString = ""
                        curString += e?.keyChar
                        lettersCSV = textField.text + ";\n"
                        lettersCSV += e?.keyChar + ";"
                        if (!checkBoth.isSelected) lettersCSV += "\n" //если выбран режим только Typed
                        startTime = System.currentTimeMillis()
                    } else { //если первый символ не правильный
                        // val text = textArea.text
                        SwingUtilities.invokeLater(Runnable() {  //то не печатаем его
                            run() {
                                textArea.text = ""  //т.е. делаем область пустой
                            }
                        })
                    }
                } else {  //для остальных символов
                    val curTime = System.currentTimeMillis()
                    val timeForLetter = curTime - startTime
                    println("time from last char for ${e?.keyChar} = ${timeForLetter}")
                    startTime = curTime
                    lettersCSV += e!!.keyChar + ";$timeForLetter"
                    if (!checkBoth.isSelected) lettersCSV += "\n" //если выбран режим только Typed
                    curString += e?.keyChar
                }
                if (!checkBoth.isSelected && curString.equals(textField.text)) {  //если выбран режим только Typed
                    lettersCSV += ";\n"                             //то записываем всё в файл
                    println("from keyTyped lettersCSV = $lettersCSV")
                    File(fioField.text + ".csv").appendText(lettersCSV)
                    JOptionPane.showMessageDialog(mainWindow, "Data is written!")
                    textField.text = ""
                    textArea.text = ""
                }
                if (curString.length == textField.text.length && !curString.equals(textField.text)) { //если введенный текст не равен исходному
                    JOptionPane.showMessageDialog(
                        mainWindow,
                        "Entered text doesn't match generated, repeat it, please."
                    )
                    textArea.text = ""
                    firstKey = true
                }
            }
        }

        override fun keyPressed(e: KeyEvent?) {
            super.keyPressed(e)
            if (e!!.keyChar.code != 65535)  //если не Shift
                if (radioBut2.isSelected || checkBoth.isSelected) { //и выбран второй режим или оба режима
                    pressTime = System.currentTimeMillis()
                }
        }

        override fun keyReleased(e: KeyEvent?) {
            super.keyReleased(e)
//            println("e = ${e!!.keyChar.code}")
            if (e!!.keyChar.code != 65535)  //если не Shift
                if (radioBut2.isSelected || checkBoth.isSelected) {  //и выбран второй режим или оба режима
                    releaseTime = System.currentTimeMillis()
                    val timePressRelease = releaseTime - pressTime
                    println("time for Press/Release for ${e?.keyChar} = ${timePressRelease}")
                    if (!checkBoth.isSelected) { //если выбран режим только Press / Release
                        curString += e?.keyChar
                        lettersCSV += e!!.keyChar
                    }
                    lettersCSV += ";$timePressRelease\n"
                    if (curString.equals(textField.text)) {//если строка введена вся, то записываем всё в файл
                        lettersCSV += ";\n"
                        println("lettersCSV = $lettersCSV")
                        File(fioField.text + ".csv").appendText(lettersCSV)
                        JOptionPane.showMessageDialog(mainWindow, "Data is written!")
                        textField.text = ""
                        textArea.text = ""
                    }
                    if (curString.length == textField.text.length && !curString.equals(textField.text)) { //если введенный текст не равен исходному
                        JOptionPane.showMessageDialog(
                            mainWindow,
                            "Entered text doesn't match generated, repeat it, please."
                        )
                        textArea.text = ""
                        firstKey = true
                    }
                }
        }
    })

    mainWindow.add(northBox, BorderLayout.NORTH)
    mainWindow.add(textArea, BorderLayout.CENTER)
    mainWindow.size = Dimension(800, 600)
    mainWindow.setLocationRelativeTo(null)
    mainWindow.isVisible = true
//    val currentLocale = Locale.getDefault()
//    println("displayLanguage = " + currentLocale.displayLanguage)
//    println("currentLocale.language = " + currentLocale.language)
//    println("lang = "+System.getProperty("user.language"))
}