package day9

import utils.InputReader
import kotlin.math.abs
import kotlin.math.max

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(9, args)

    val pts = lines
        .filter { it.isNotEmpty() }
        .map { line ->
            val (x, y) = line.split(',').map { it.toInt() }
            Pair(x, y)
        }

    // Solve Problem:
    var best = 0L

    val n = pts.size
    for (i in 0 until n) {
        for (j in i + 1 until n) {
            val (x1, y1) = pts[i]
            val (x2, y2) = pts[j]

            val dx = abs(x2 - x1).toLong()
            val dy = abs(y2 - y1).toLong()

            if (dx > 0 && dy > 0) {
                best = max(best, (dx + 1) * (dy + 1))
            }
        }
    }

    println(best)
}