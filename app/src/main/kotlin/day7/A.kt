package day7

import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(7, args)
    val width = lines.first().length
    val grid = lines.map { line ->
        require(line.length == width) { "All rows must share the same width" }
        line.toCharArray()
    }

    val start = findStart(grid) ?: error("No starting position 'S' found")
    var activeColumns = mutableSetOf(start.second)
    var splits = 0L

    // Solve Problem:
    for (row in start.first + 1 until grid.size) {
        if (activeColumns.isEmpty()) break
        var rowBeams = activeColumns
        while (true) {
            var anySplit = false
            val nextColumns = mutableSetOf<Int>()
            for (col in rowBeams) {
                if (col !in 0 until width) continue
                when (grid[row][col]) {
                    '^' -> {
                        splits++
                        anySplit = true
                        val left = col - 1
                        val right = col + 1
                        if (left >= 0) nextColumns.add(left)
                        if (right < width) nextColumns.add(right)
                    }
                    else -> nextColumns.add(col)
                }
            }
            if (!anySplit) {
                rowBeams = nextColumns
                break
            }
            rowBeams = nextColumns
        }
        activeColumns = rowBeams
    }
    println(splits)
}

private fun findStart(grid: List<CharArray>): Pair<Int, Int>? {
    grid.forEachIndexed { row, chars ->
        val column = chars.indexOf('S')
        if (column != -1) return row to column
    }
    return null
}