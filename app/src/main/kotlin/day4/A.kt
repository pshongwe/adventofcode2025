package day4

import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(4, args)
    val grid = lines.map { it.toCharArray() }.toTypedArray()
    println("Initial Grid: ")
    println("----------")
    grid.forEach { println(it.concatToString()) }
    println("----------")

    // Solve Problem

    // Make a copy to write the result into to avoid errors
    val newGrid = Array(grid.size) { r -> grid[r].clone() }

    var count = 0
    grid.forEachCell { r, c, value ->
        val hasFourOrMore = grid.hasAtLeastNNeighborsOf(r, c, '@', 4)
        if (value == '@' && !hasFourOrMore) {
            newGrid[r][c] = 'x'
            count++
        }
    }

    // Copy result back for final print
    for (r in grid.indices) {
        grid[r] = newGrid[r]
    }

    println("Result Grid: ")
    println("----------")
    grid.forEach { println(it.concatToString()) }
    println("----------")
    println("Total rolls: $count")
}

fun Array<CharArray>.hasAtLeastNNeighborsOf(
    r: Int, c: Int, target: Char, n: Int
): Boolean {
    val directions8 = listOf(
        -1 to -1, -1 to 0, -1 to 1,
        0 to -1,          0 to 1,
        1 to -1,  1 to 0,  1 to 1
    )

    var count = 0
    for ((dr, dc) in directions8) {
        val nr = r + dr
        val nc = c + dc

        if (nr in indices && nc in this[0].indices && this[nr][nc] == target) {
            count++
            if (count >= n) return true
        }
    }
    return false
}

fun Array<CharArray>.forEachCell(action: (r: Int, c: Int, value: Char) -> Unit) {
    for (r in indices) {
        for (c in this[r].indices) {
            action(r, c, this[r][c])
        }
    }
}

