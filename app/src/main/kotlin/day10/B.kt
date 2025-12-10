package day10

import java.math.BigInteger
import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(10, args)

    // Solve Problem:
    var totalPresses = 0

    lines.forEachIndexed { index, line ->
        val machine = parseJoltageMachine(line)
        val presses = solveMinimumPresses(machine)
        println("Machine ${index + 1}: $presses presses")
        totalPresses += presses
    }

    println("Total button presses: $totalPresses")
}

private data class JoltageMachine(
    val targetJoltages: IntArray,
    val buttons: List<IntArray>
)

private fun parseJoltageMachine(line: String): JoltageMachine {
    val joltagePart = line.substringAfter("{").substringBefore("}")
    val targetJoltages = joltagePart.split(',').map { it.trim().toInt() }.toIntArray()

    val buttonPattern = Regex("""\(([0-9,]+)\)""")
    val buttons = buttonPattern.findAll(line)
        .map { match ->
            match.groupValues[1]
                .split(',')
                .filter { it.isNotEmpty() }
                .map { it.trim().toInt() }
                .distinct()
                .toIntArray()
        }
        .toList()

    return JoltageMachine(targetJoltages, buttons)
}

private fun solveMinimumPresses(machine: JoltageMachine): Int {
    if (machine.buttons.isEmpty()) return if (machine.targetJoltages.all { it == 0 }) 0 else error("No buttons available")

    val coefficients = buildCoefficientMatrix(machine)
    val rrefResult = reducedRowEchelon(coefficients, machine.targetJoltages)

    return enumerateSolutions(coefficients, machine, rrefResult)
}

private fun buildCoefficientMatrix(machine: JoltageMachine): Array<IntArray> {
    val rows = machine.targetJoltages.size
    val cols = machine.buttons.size
    val matrix = Array(rows) { IntArray(cols) }

    machine.buttons.forEachIndexed { buttonIdx, counters ->
        counters.forEach { counterIdx ->
            matrix[counterIdx][buttonIdx] = 1
        }
    }

    return matrix
}

private data class RrefResult(
    val matrix: Array<Array<Rational>>,
    val pivotColumns: List<Int>,
    val pivotRows: List<Int>,
    val hasSolution: Boolean
)

private fun reducedRowEchelon(coefficients: Array<IntArray>, targets: IntArray): RrefResult {
    val numRows = coefficients.size
    val numVars = coefficients[0].size
    val numCols = numVars + 1
    val matrix = Array(numRows) { row ->
        Array(numCols) { col ->
            if (col < numVars) Rational.of(coefficients[row][col].toLong())
            else Rational.of(targets[row].toLong())
        }
    }

    var pivotRow = 0
    val pivotColumns = mutableListOf<Int>()
    val pivotRows = mutableListOf<Int>()

    for (col in 0 until numVars) {
        var candidateRow = pivotRow
        while (candidateRow < numRows && matrix[candidateRow][col].isZero()) {
            candidateRow++
        }
        if (candidateRow == numRows) continue

        if (candidateRow != pivotRow) {
            val tmp = matrix[pivotRow]
            matrix[pivotRow] = matrix[candidateRow]
            matrix[candidateRow] = tmp
        }

        val pivotValue = matrix[pivotRow][col]
        for (c in col until numCols) {
            matrix[pivotRow][c] = matrix[pivotRow][c] / pivotValue
        }

        for (row in 0 until numRows) {
            if (row == pivotRow) continue
            val factor = matrix[row][col]
            if (factor.isZero()) continue
            for (c in col until numCols) {
                matrix[row][c] = matrix[row][c] - factor * matrix[pivotRow][c]
            }
        }

        pivotColumns.add(col)
        pivotRows.add(pivotRow)
        pivotRow++
        if (pivotRow == numRows) break
    }

    var hasSolution = true
    for (row in 0 until numRows) {
        val allZeroCoefficients = (0 until numVars).all { matrix[row][it].isZero() }
        if (allZeroCoefficients && !matrix[row][numVars].isZero()) {
            hasSolution = false
            break
        }
    }

    return RrefResult(matrix, pivotColumns, pivotRows, hasSolution)
}

private fun enumerateSolutions(
    coefficients: Array<IntArray>,
    machine: JoltageMachine,
    rrefResult: RrefResult
): Int {
    val numVars = machine.buttons.size
    val assigned = IntArray(numVars)
    val freeVariables = (0 until numVars).filter { it !in rrefResult.pivotColumns }
    val bounds = computeButtonUpperBounds(machine)

    var best = Int.MAX_VALUE

    if (freeVariables.isEmpty()) {
        if (assignPivotValues(rrefResult, freeVariables, assigned)) {
            if (respectBounds(assigned, bounds) && verifySolution(coefficients, machine.targetJoltages, assigned)) {
                best = assigned.sum()
            }
        }
        return best
    }

    fun search(freeIdx: Int, currentSum: Int) {
        if (currentSum >= best) return
        if (freeIdx == freeVariables.size) {
            if (!assignPivotValues(rrefResult, freeVariables, assigned)) return
            if (!respectBounds(assigned, bounds)) return
            if (!verifySolution(coefficients, machine.targetJoltages, assigned)) return
            best = minOf(best, assigned.sum())
            return
        }

        val varIndex = freeVariables[freeIdx]
        val maxValue = bounds[varIndex]
        for (value in 0..maxValue) {
            assigned[varIndex] = value
            search(freeIdx + 1, currentSum + value)
        }
        assigned[varIndex] = 0
    }

    search(0, 0)
    return best
}

private fun assignPivotValues(
    rrefResult: RrefResult,
    freeVars: List<Int>,
    assigned: IntArray
): Boolean {
    val augmentedCol = rrefResult.matrix.first().size - 1
    for ((idx, pivotCol) in rrefResult.pivotColumns.withIndex()) {
        val rowIdx = rrefResult.pivotRows[idx]
        var value = rrefResult.matrix[rowIdx][augmentedCol]
        for (freeVar in freeVars) {
            val coeff = rrefResult.matrix[rowIdx][freeVar]
            if (!coeff.isZero()) {
                value -= coeff * Rational.of(assigned[freeVar].toLong())
            }
        }
        if (!value.isInteger()) return false
        val intValue = value.toLongExact().toInt()
        if (intValue < 0) return false
        assigned[pivotCol] = intValue
    }
    return true
}

private fun computeButtonUpperBounds(machine: JoltageMachine): IntArray {
    val bounds = IntArray(machine.buttons.size)
    machine.buttons.forEachIndexed { idx, counters ->
        bounds[idx] = if (counters.isEmpty()) 0 else counters.minOf { counter -> machine.targetJoltages[counter] }
    }
    return bounds
}

private fun respectBounds(values: IntArray, bounds: IntArray): Boolean {
    for (i in values.indices) {
        if (values[i] < 0 || values[i] > bounds[i]) return false
    }
    return true
}

private fun verifySolution(coefficients: Array<IntArray>, targets: IntArray, solution: IntArray): Boolean {
    for (row in targets.indices) {
        var sum = 0
        for (col in solution.indices) {
            sum += coefficients[row][col] * solution[col]
        }
        if (sum != targets[row]) return false
    }
    return true
}

private class Rational private constructor(
    val numerator: BigInteger,
    val denominator: BigInteger
) {
    companion object {
        private val ZERO = Rational(BigInteger.ZERO, BigInteger.ONE)
        fun of(value: Long): Rational = Rational(BigInteger.valueOf(value), BigInteger.ONE)
        fun of(num: BigInteger, den: BigInteger): Rational {
            var n = num
            var d = den
            if (d.signum() < 0) {
                n = n.negate()
                d = d.negate()
            }
            val gcd = n.abs().gcd(d)
            if (gcd != BigInteger.ONE) {
                n = n.divide(gcd)
                d = d.divide(gcd)
            }
            return Rational(n, d)
        }
    }

    fun isZero(): Boolean = numerator.signum() == 0
    fun isInteger(): Boolean = denominator == BigInteger.ONE
    fun toLongExact(): Long {
        return numerator.longValueExact()
    }

    operator fun plus(other: Rational): Rational =
        of(numerator * other.denominator + other.numerator * denominator, denominator * other.denominator)

    operator fun minus(other: Rational): Rational =
        of(numerator * other.denominator - other.numerator * denominator, denominator * other.denominator)

    operator fun times(other: Rational): Rational =
        of(numerator * other.numerator, denominator * other.denominator)

    operator fun div(other: Rational): Rational =
        of(numerator * other.denominator, denominator * other.numerator)
}
