import java.awt.*
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.swing.*
import kotlin.random.Random
import kotlin.streams.toList

fun main(args: Array<String>) {
    println("Hello World!")
    createGUI()
}

fun createGUI() {
    var charTimingsArray = arrayListOf<CharTimings>()
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
    val keyModeGroup = ButtonGroup()  //группа для радиокнопок
    val radioBut1 = JRadioButton("Typed")  //режим замера времени только между нажатиями клавиш
    val radioBut2 = JRadioButton("Press / Release")  //режим замера сколько была нажата клавиша
    radioBut2.isSelected = true //по умолчанию второй режим
    keyModeGroup.add(radioBut1)
    keyModeGroup.add(radioBut2)
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
    for (element in keyModeGroup.elements) {  //делаем неактивными радиокнопки
        element.isEnabled = !checkBoth.isSelected
    }
    checkBoth.addActionListener {           //переключаем активность радиокнопок в зависимости от чекбокса checkBoth
        for (element in keyModeGroup.elements) {
            element.isEnabled = !checkBoth.isSelected
        }
    }

    val modeBox = Box(BoxLayout.X_AXIS)
    modeBox.add(JLabel("Mode: "))
    val operatingModeGroup = ButtonGroup()
    val freeMode = JRadioButton("Free Mode")
    freeMode.isSelected = true
    operatingModeGroup.add(freeMode)
    modeBox.add(freeMode)
    val fromFileMode = JRadioButton("From file")
    operatingModeGroup.add(fromFileMode)
    modeBox.add(fromFileMode)
    val freeModeBox = Box(BoxLayout.X_AXIS)
    val fileModeBox = Box(BoxLayout.X_AXIS)
    fileModeBox.isVisible = false
    fromFileMode.addActionListener {
        freeModeBox.isVisible = false
        fileModeBox.isVisible = true
    }
    freeMode.addActionListener {
        freeModeBox.isVisible = true
        fileModeBox.isVisible = false
    }

    northBox.add(modeBox)

    val textArea = JTextArea()
    var firstKey = true  //признак нажатия первого символа
    val lettersBox = Box(BoxLayout.X_AXIS)  //коробка с буквами
    val textField = JTextField(50)
    textField.font = Font("Tahoma", Font.PLAIN, 16)
    textField.addKeyListener(object : KeyAdapter() { //для свободного ввода букв
        override fun keyTyped(e: KeyEvent?) {
            textArea.isEnabled = true
            firstKey = true
        }
    })
    textField.addFocusListener(object : FocusAdapter(){ //для инициализации charTimingsArray после свободного ввода строки
        override fun focusLost(e: FocusEvent?) {
            super.focusLost(e)
            charTimingsArray = arrayListOfCharTimings(textField.text)
        }
    })

    val nextStringFromFile = JButton("Next String")
    nextStringFromFile.isEnabled = false
    val fileLabel = JLabel("no file")
    val selectFileBtn = JButton("Select File")
    var inputStreamReader: BufferedReader? = null
    selectFileBtn.addActionListener {
        val fileDialog = FileDialog(mainWindow)
        fileDialog.mode = FileDialog.LOAD //диалог в режим открытия
        fileDialog.title = "Open File with strings" //заголовок диалога открытия
        fileDialog.setFile("*.txt") //фильтр для файлов
        fileDialog.isVisible = true //показываем диалог открытия
//если пользователь выбрал каталог и файл, т.е. они не содержат null
//это нужно, чтоб обработать отказ от открытия, иначе будет ошибка
        if (!(fileDialog.directory+fileDialog.file).contains("null")) {
            val fileName = fileDialog.directory + fileDialog.file //записываем путь к файлу
            println("fileName=$fileName") //выводим полное имя файла в консоль
            nextStringFromFile.isEnabled = true
            fileLabel.text = fileDialog.file
            inputStreamReader = File(fileName).reader().buffered()
            inputStreamReader!!.readLine()
        }
    }
    nextStringFromFile.addActionListener {
        val s = inputStreamReader!!.readLine()
        println("s = $s")
        textField.text = s.toString()
        textArea.isEnabled = true
        firstKey = true
        textField.requestFocus()
    }

    fileModeBox.add(nextStringFromFile)
    fileModeBox.add(fileLabel)
    fileModeBox.add(selectFileBtn)

    val sizeLabel = JLabel("text size: ")
    val stringLength = JTextField(5)
    stringLength.text = "5"
    val genButton = JButton("Generate text")
    freeModeBox.add(sizeLabel)
    freeModeBox.add(stringLength)
    freeModeBox.add(genButton)
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
        textField.requestFocus()
        curString = ""
        textArea.isEnabled = true
        textArea.requestFocus()
    }

    lettersBox.border = BorderFactory.createLineBorder(Color.BLUE, 5)
    lettersBox.add(textField)
//    lettersBox.add(sizeLabel)
//    lettersBox.add(stringLength)
//    lettersBox.add(genButton)
    lettersBox.add(freeModeBox)
    lettersBox.add(fileModeBox)

    northBox.add(lettersBox)
    textArea.border = BorderFactory.createLineBorder(Color.GRAY, 5)
    var startTime: Long = 0
    var pressTime: Long = 0
    var releaseTime: Long = 0
    textArea.addKeyListener(object : KeyAdapter() {
//        override fun keyTyped(e: KeyEvent?) {
//            super.keyTyped(e)
//            if (radioBut1.isSelected || checkBoth.isSelected) { //если выбран первый режим или оба режима
//                if (firstKey) {  //если первый символ
//                    if (e?.keyChar == textField.text[0]) { //и он правильный
//                        firstKey = false
//                        println("timer started")
//                        curString = ""
//                        curString += e?.keyChar
//                        lettersCSV = textField.text + ";\n"
//                        lettersCSV += e?.keyChar + ";"
//                        if (!checkBoth.isSelected) lettersCSV += "\n" //если выбран режим только Typed
//                        startTime = System.currentTimeMillis()
//                        //todo убрать keyTyped - добавить к keyPressed и keyReleased вычисление прошедшего времени
//                    // после нажатия предыдущей кнопки и результаты записывать в массив объектов, а потом - в строку для файла,
//                        //чтобы не было мешанины при быстром нажатии клавиш
//                    } else { //если первый символ не правильный
//                        // val text = textArea.text
//                        SwingUtilities.invokeLater(Runnable() {  //то не печатаем его
//                            run() {
//                                textArea.text = ""  //т.е. делаем область пустой
//                                println("Wrong letter! ${e?.keyChar} != ${textField.text[0]}  text = ${textField.text}")
//                            }
//                        })
//                    }
//                } else {  //для остальных символов
//                    val curTime = System.currentTimeMillis()
//                    val timeForLetter = curTime - startTime
//                    println("time from last char for ${e?.keyChar} = ${timeForLetter}")
//                    startTime = curTime
//                    lettersCSV += e!!.keyChar + ";$timeForLetter"
//                    if (!checkBoth.isSelected) lettersCSV += "\n" //если выбран режим только Typed
//                    curString += e?.keyChar
//                }
//                if (!checkBoth.isSelected && curString.equals(textField.text)) {  //если выбран режим только Typed
//                    lettersCSV += ";\n"                             //то записываем всё в файл
//                    println("from keyTyped lettersCSV = $lettersCSV")
//                    File(fioField.text + ".csv").appendText(lettersCSV)
//                    JOptionPane.showMessageDialog(mainWindow, "Data is written!")
//                    textField.text = ""
//                    textArea.text = ""
//                }
//                if (curString.length == textField.text.length && !curString.equals(textField.text)) { //если введенный текст не равен исходному
//                    JOptionPane.showMessageDialog(
//                        mainWindow,
//                        "Entered text doesn't match generated, repeat it, please."
//                    )
//                    textArea.text = ""
//                    firstKey = true
//                }
//            }
//        }

        override fun keyPressed(e: KeyEvent?) {
            super.keyPressed(e)
            if (e!!.keyChar.code != 65535)  //если не Shift
                if (radioBut2.isSelected || checkBoth.isSelected) { //и выбран второй режим или оба режима
                    //todo сделать запись в массив таймингов времени нажатия
                    pressTime = System.currentTimeMillis()
                    val charTimingTemp = charTimingsArray.find {it.letter == e!!.keyChar && it.timePress == 0L}
                    charTimingTemp?.timePress = pressTime
//                    if (!checkBoth.isSelected) { //если выбран режим только Press / Release
                        curString += e?.keyChar
                       // lettersCSV += e!!.keyChar
//                    }
                    if ( firstKey && e?.keyChar == textField.text[0]) { //если символ первый и он правильный
                        firstKey = false
                        println("timer started")
                       // curString = ""
                        charTimingTemp?.first = true
                    }
                }
//            printCharTimingsLetters(charTimingsArray)
        }

        override fun keyReleased(e: KeyEvent?) {
            super.keyReleased(e)
//            println("e = ${e!!.keyChar.code}")
                //todo сделать запись в массив таймингов времени отпускания и разницы между прошлой клавишей - в завимости
            //от разницы - от прошлого press или release
            if (e!!.keyChar.code != 65535)  //если не Shift
                if (radioBut2.isSelected || checkBoth.isSelected) {  //и выбран второй режим или оба режима
                    releaseTime = System.currentTimeMillis()
                   // val timePressRelease = releaseTime - pressTime
                    val charTimingTemp = charTimingsArray.find {it.letter == e!!.keyChar && it.timeRelease == 0L}
                    charTimingTemp?.timeRelease = releaseTime
                    printCharTimingsLetters(charTimingsArray)
//                    println("time for Press/Release for ${e?.keyChar} = ${timePressRelease}")
//                    if (!checkBoth.isSelected) { //если выбран режим только Press / Release
//                        curString += e?.keyChar
//                        lettersCSV += e!!.keyChar
//                    }
                   // lettersCSV += ";$timePressRelease\n"
                    if (curString.equals(textField.text) && e!!.keyChar == textField.text.last()) {//если строка введена вся, то записываем всё в файл
                        println("last char = ${textField.text.last()}")
                        lettersCSV = textField.text + ";\n"
                        var i = 0
                        charTimingsArray.forEach {
                            it.timeTyped = (it.timeRelease - it.timePress).toInt()
                            lettersCSV+=it.letter+";" + it.timeTyped + ";"
                            if (it.first) {
                                lettersCSV+="\n"
                            } else {
                                val timeFromPrevious = it.timePress - charTimingsArray.get(i-1).timePress
                                lettersCSV+= "$timeFromPrevious;\n"
                            }
                            i++
                        }
                        lettersCSV += ";\n"
//                        println("lettersCSV = $lettersCSV")
                        File(fioField.text + ".csv").appendText(lettersCSV)
                        JOptionPane.showMessageDialog(mainWindow, "Data is written!")
                        textField.text = ""
                        textArea.text = ""
                        curString = ""
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