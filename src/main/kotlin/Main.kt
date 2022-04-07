import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import kotlin.random.Random

fun main(args: Array<String>) {
    println("Hello World!")
    createGUI()
}

fun createGUI(){
    val mainWindow = JFrame("LettersTiming")
    mainWindow.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val northBox = Box(BoxLayout.X_AXIS)
    val textField = JTextField(50)
    val sizeLabel = JLabel("text size: ")
    val fieldLength = JTextField("10")
    val genButton = JButton("Generate text")
    genButton.addActionListener {
        var textString: String
        var symbols = CharArray(fieldLength.text.toInt())
        for (i in 0..fieldLength.text.toInt()-1){
            val symbol = Random.nextInt(35, 126).toChar()
            print("$symbol")
            symbols[i] = symbol
        }
        textString = symbols.concatToString()
        println("textString = $textString")
        textField.text = textString
    }

    northBox.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));
    northBox.add(textField)
    northBox.add(sizeLabel)
    northBox.add(fieldLength)
    northBox.add(genButton)
    val textArea = JTextArea()
    textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
    mainWindow.add(northBox, BorderLayout.NORTH)
    mainWindow.add(textArea, BorderLayout.CENTER)
    mainWindow.size = Dimension(800, 600)
    mainWindow.setLocationRelativeTo(null)
    mainWindow.isVisible = true
}