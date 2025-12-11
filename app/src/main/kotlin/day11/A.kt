package day11

import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(11, args)

    val graph = parseLines(lines)
    graph.forEach { println(it) }

    // Solve Problem:
    val memo = mutableMapOf<String, Int>()
    val totalPaths = countPaths("you", "out", graph, memo)
    println("Paths from you to out: $totalPaths")
}

private fun parseLines(lines: List<String>): Map<String, List<String>> {
    return lines.associate { line ->
        val (name, rhs) = line.split(":").map { it.trim() }
        val children = rhs.split(" ")
            .filter { it.isNotBlank() }
        name to children
    }
}

private fun countPaths(
    start: String,
    target: String,
    graph: Map<String, List<String>>,
    memo: MutableMap<String, Int>
): Int {
    if (start == target) return 1
    if (start !in graph) return 0

    memo[start]?.let { return it }

    val sum = graph[start]!!
        .sumOf { next -> countPaths(next, target, graph, memo) }

    memo[start] = sum
    return sum
}