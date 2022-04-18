data class CharTimings(val letter: Char, var timeTyped: Int = 0, var timePress: Long = 0, var timeRelease: Long = 0)

fun printCharTimingsLetters(arr: ArrayList<CharTimings>){  //печать букв из массива с CharTimings
    println("array chars = Press\t Release")
    arr.forEach {
        println("\t${it.letter}  ${it.timePress}  ${it.timeRelease}")
    }
   // println()
}

fun arrayListOfCharTimings(text: String): ArrayList<CharTimings>{ //создание массива с CharTimings
    val charTimingsArray = arrayListOf<CharTimings>()
    text.forEach {
        charTimingsArray.add(CharTimings(it))
    }
    printCharTimingsLetters(charTimingsArray)
    return charTimingsArray
}

fun charTimingsArrayToFile(fileName: String, arr: ArrayList<CharTimings>){

}