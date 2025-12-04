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
    var removed = 0
    do {
        removed = 0
        newGrid.forEachCell { r, c, value ->
            val hasFourOrMore = newGrid.hasAtLeastNNeighborsOf(r, c, '@', 4)
            if (value == '@' && !hasFourOrMore) {
                newGrid[r][c] = 'x'
                count++
                removed++
            }
        }
    } while (removed != 0)

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