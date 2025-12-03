package day3

import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(3, args)
    println("Read ${lines.size} lines")

    println("First 5 ranges:")
    lines.take(4).forEachIndexed { i, l -> println("${i + 1}: $l") }

    // Solve Problem
    var count = 0

    for (line in lines) {
        val best = bestTwoDigitNumber(line)
        count += best
    }
    println("Total count = $count")
}

fun bestTwoDigitNumber(line: String): Int {
    val digits = line.map { it.digitToInt() }
    var best = -1

    for (i in 0 until digits.size - 1) {
        val first = digits[i]
        for (j in i + 1 until digits.size) {
            val second = digits[j]
            val candidate = first * 10 + second
            if (candidate > best) {
                best = candidate
            }
        }
    }

    return best
}
