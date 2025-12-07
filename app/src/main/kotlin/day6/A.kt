package day6

import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(6, args)

    val numberLines = lines.dropLast(1)
    val operations = lines.last().trim().split(Regex("\\s+")).map { it.first() }
    val grid = numberLines.map { row ->
        row.trim().split(Regex("\\s+")).map { it.toLong() }
    }
    val columnCount = grid.first().size

    // Solve Problem
    val columnResults = (0 until columnCount).map { columnIndex ->
        val columnValues = grid.map { row -> row[columnIndex] }
        val op = operations[columnIndex]
        val columnResult = columnValues.reduce { acc, value -> applyOperation(acc, value, op) }
        println("${columnValues.joinToString(" $op ")} = $columnResult")
        columnResult
    }

    val grandTotal = columnResults.sum()
    println("Grand Total = $grandTotal")
}

private fun applyOperation(left: Long, right: Long, op: Char): Long = when (op) {
    '+' -> left + right
    '-' -> left - right
    '*' -> left * right
    '/' -> if (right == 0L) left else left / right
    else -> error("Unsupported operation '$op'")
}