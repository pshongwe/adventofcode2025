package day7

import utils.InputReader
import java.math.BigInteger

fun main(args: Array<String>) {
    val lines = InputReader.readLinesForDay(7, args)
    if (lines.isEmpty()) {
        println(0)
        return
    }

    val width = lines.first().length
    val grid = lines.map { line ->
        require(line.length == width) { "All rows must share the same width" }
        line.toCharArray()
    }

    val start = findStart(grid) ?: error("No starting position 'S' found")
    var active = mutableMapOf(start.second to BigInteger.ONE)

    for (row in start.first + 1 until grid.size) {
        if (active.isEmpty()) break
        var current = active
        while (true) {
            var anySplit = false
            val next = mutableMapOf<Int, BigInteger>()
            for ((col, count) in current) {
                if (col !in 0 until width) continue
                when (grid[row][col]) {
                    '^' -> {
                        anySplit = true
                        val left = col - 1
                        val right = col + 1
                        if (left >= 0) next[left] = (next[left] ?: BigInteger.ZERO) + count
                        if (right < width) next[right] = (next[right] ?: BigInteger.ZERO) + count
                    }
                    else -> {
                        next[col] = (next[col] ?: BigInteger.ZERO) + count
                    }
                }
            }
            if (!anySplit) {
                current = next
                break
            }
            current = next
        }
        active = current
    }

    val totalTimelines = active.values.fold(BigInteger.ZERO, BigInteger::add)
    println(totalTimelines)
}

private fun findStart(grid: List<CharArray>): Pair<Int, Int>? {
    grid.forEachIndexed { row, chars ->
        val column = chars.indexOf('S')
        if (column != -1) return row to column
    }
    return null
}
