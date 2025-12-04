package day3

import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(3, args)
    println("Read ${lines.size} lines")

    println("First 5 ranges:")
    lines.take(4).forEachIndexed { i, l -> println("${i + 1}: $l") }

    // Solve Problem
    var count = 0L

    for (line in lines) {
        val best = bestTwelveDigitNumber(line)

        count += best.toLong()
    }
    println("Total count = $count")
}

fun bestTwelveDigitNumber(line: String): String {
    return bestKDigitNumber(line, 12)
}

fun bestKDigitNumber(line: String, k: Int): String {
    val digits = line.map { it.digitToInt() }
    val n = digits.size

    require(k <= n) { "k cannot be larger than the number of digits" }

    val stack = ArrayDeque<Int>()
    var toDrop = n - k

    for (d in digits) {
        while (stack.isNotEmpty() && toDrop > 0 && stack.last() < d) {
            stack.removeLast()
            toDrop--
        }
        stack.addLast(d)
    }
    return stack.take(k).joinToString("")
}

