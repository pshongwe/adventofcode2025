package day10

import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(10, args)

    // Solve Problem:
    var totalPresses = 0

    for ((idx, line) in lines.withIndex()) {
        val machine = parseMachine(line)
        val minPresses = solveMinimumPresses(machine)
        println("Machine ${idx + 1}: $minPresses presses")
        totalPresses += minPresses
    }
    println("Button presses: $totalPresses")
}

private data class Machine(
    val targetLights: List<Boolean>,
    val buttons: List<List<Int>>
)

private fun parseMachine(line: String): Machine {
    val lightPattern = line.substringAfter("[").substringBefore("]")
    val targetLights = lightPattern.map { it == '#' }

    val buttonPattern = Regex("""\(([0-9,]+)\)""")
    val buttons = buttonPattern.findAll(line).map { match ->
        match.groupValues[1].split(",").map { it.toInt() }
    }.toList()

    return Machine(targetLights, buttons)
}

private fun solveMinimumPresses(machine: Machine): Int {
    val numLights = machine.targetLights.size
    val numButtons = machine.buttons.size

    val matrix = Array(numLights) { IntArray(numButtons + 1) }

    for (buttonIdx in machine.buttons.indices) {
        for (lightIdx in machine.buttons[buttonIdx]) {
            matrix[lightIdx][buttonIdx] = 1
        }
    }

    for (lightIdx in machine.targetLights.indices) {
        matrix[lightIdx][numButtons] = if (machine.targetLights[lightIdx]) 1 else 0
    }

    val solutions = gaussianEliminationGF2WithAllSolutions(matrix, numButtons)

    return solutions.minOfOrNull { it.count { v -> v == 1 } } ?: Int.MAX_VALUE
}

private fun gaussianEliminationGF2WithAllSolutions(matrix: Array<IntArray>, numVars: Int): List<IntArray> {
    val numRows = matrix.size
    val numCols = matrix[0].size

    val m = matrix.map { it.copyOf() }.toTypedArray()

    var currentRow = 0
    val pivotCols = mutableListOf<Int>()

    for (col in 0 until numVars) {
        var pivotRow = -1
        for (row in currentRow until numRows) {
            if (m[row][col] == 1) {
                pivotRow = row
                break
            }
        }

        if (pivotRow == -1) continue

        pivotCols.add(col)

        if (pivotRow != currentRow) {
            val temp = m[currentRow]
            m[currentRow] = m[pivotRow]
            m[pivotRow] = temp
        }

        for (row in 0 until numRows) {
            if (row != currentRow && m[row][col] == 1) {
                for (c in 0 until numCols) {
                    m[row][c] = (m[row][c] + m[currentRow][c]) % 2
                }
            }
        }

        currentRow++
    }

    for (row in 0 until numRows) {
        val allZero = (0 until numVars).all { m[row][it] == 0 }
        if (allZero && m[row][numVars] == 1) {
            return emptyList() // No solution
        }
    }

    val freeVars = (0 until numVars).filter { it !in pivotCols }

    val numFreeVars = freeVars.size
    val solutions = mutableListOf<IntArray>()

    for (mask in 0 until (1 shl numFreeVars)) {
        val solution = IntArray(numVars)

        for (i in freeVars.indices) {
            solution[freeVars[i]] = (mask shr i) and 1
        }

        for (row in pivotCols.indices) {
            val col = pivotCols[row]
            var value = m[row][numVars]

            for (freeVar in freeVars) {
                value = (value + m[row][freeVar] * solution[freeVar]) % 2
            }

            solution[col] = value
        }

        solutions.add(solution)
    }
    return solutions
}