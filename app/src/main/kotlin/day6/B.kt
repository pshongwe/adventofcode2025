package day6

import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val rawLines = InputReader.readLinesForDay(6, args)

    val paddedLines = padLines(rawLines)
    val digitRows = paddedLines.dropLast(1)
    val operatorRow = paddedLines.last()

    val problems = splitIntoProblems(digitRows, operatorRow)

    // Solve Problem
    val columnResults = problems.asReversed().map { problem ->
        val values = problem.columns.asReversed().map { column ->
            val cleaned = column.filterNot { it.isWhitespace() }
            cleaned.toLong()
        }
        val op = problem.operator
        val columnResult = values.reduce { acc, value -> applyOperation(acc, value, op) }
        println("${values.joinToString(" $op ")} = $columnResult")
        columnResult
    }

    val grandTotal = columnResults.sum()
    println("Grand Total = $grandTotal")
}

private data class Problem(val columns: List<String>, val operator: Char)

private fun padLines(lines: List<String>): List<String> {
    val width = lines.maxOf { it.length }
    return lines.map { it.padEnd(width, ' ') }
}

private fun splitIntoProblems(digitRows: List<String>, operatorRow: String): List<Problem> {
    val width = operatorRow.length
    val problems = mutableListOf<Problem>()
    val currentColumns = mutableListOf<String>()
    var currentOperator: Char? = null

    fun flushCurrent() {
        if (currentColumns.isNotEmpty()) {
            val op = currentOperator ?: error("Problem missing operator")
            problems += Problem(currentColumns.toList(), op)
        }
        currentColumns.clear()
    }

    for (index in 0 until width) {
        val columnDigits = buildString {
            digitRows.forEach { row -> append(row[index]) }
        }
        val operator = operatorRow[index]
        val isSeparator = columnDigits.all { it.isWhitespace() } && operator.isWhitespace()
        if (isSeparator) {
            flushCurrent()
            continue
        }

        if (!operator.isWhitespace()) {
            currentOperator = operator
        }
        currentColumns += columnDigits
    }

    flushCurrent()

    return problems
}

private fun applyOperation(left: Long, right: Long, op: Char): Long = when (op) {
    '+' -> left + right
    '-' -> left - right
    '*' -> left * right
    '/' -> if (right == 0L) left else left / right
    else -> error("Unsupported operation '$op'")
}