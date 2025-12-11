package day11

import utils.InputReader

private data class StateKey(val node: String, val seenDac: Boolean, val seenFft: Boolean)

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(11, args)

    val graph = parseLines(lines)

    // Solve Problem:
    val memo = mutableMapOf<StateKey, Long>()
    val count = countPaths(
        current = "svr",
        graph = graph,
        seenDac = false,
        seenFft = false,
        memo = memo
    )

    println("Number of paths from svr to out that visit BOTH dac and fft = $count")
}

private fun parseLines(lines: List<String>): Map<String, List<String>> =
    lines.associate { line ->
        val (name, rhs) = line.split(":").map { it.trim() }
        val children = rhs.split(" ").filter { it.isNotBlank() }
        name to children
    }

private fun countPaths(
    current: String,
    graph: Map<String, List<String>>,
    seenDac: Boolean,
    seenFft: Boolean,
    memo: MutableMap<StateKey, Long>
): Long {
    val newSeenDac = seenDac || current == "dac"
    val newSeenFft = seenFft || current == "fft"

    if (current == "out") {
        return if (newSeenDac && newSeenFft) 1 else 0
    }

    val key = StateKey(current, newSeenDac, newSeenFft)
    memo[key]?.let { return it }

    val children = graph[current] ?: emptyList()

    var total = 0L
    for (child in children) {
        total += countPaths(child, graph, newSeenDac, newSeenFft, memo)
    }
    memo[key] = total
    return total
}