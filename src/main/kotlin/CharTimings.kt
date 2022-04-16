data class CharTimings(val letter: Char, val timeTyped: Int = 0, val timePress: Long = 0, val timeRelease: Long = 0)

fun printCharTimingsLetters(arr: ArrayList<CharTimings>){  //печать букв из массива с CharTimings
    print("array chars = ")
    arr.forEach {
        print("${it.letter}")
    }
    println()
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