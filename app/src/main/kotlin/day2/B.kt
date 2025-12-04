package day2

import utils.InputReader

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(2, args)
    println("Read ${lines.size} lines")

    val ranges: List<Range> = lines.flatMap { line ->
        line.trim()
            .split(',')
            .mapNotNull { part ->
                val tokens = part.split('-')
                if (tokens.size == 2) {
                    val start = tokens[0].toLongOrNull()
                    val end = tokens[1].toLongOrNull()
                    if (start != null && end != null) Range(start, end) else null
                } else null
            }
    }

    println("Parsed ${ranges.size} ranges")
    println("First 5 ranges:")
    ranges.take(5).forEachIndexed { i, r -> println("${i + 1}: ${r.start}-${r.end}") }

    // Solve Problem:
    var count: Long = 0

    ranges.take(ranges.size).forEach { range ->
        for (step in range.start..range.end) {
            if (isInvalidID(step.toString())) count += step
        }
    }
    println("Total Invalid ID's: $count")
}

fun isInvalidID(id: String): Boolean {
    for (patternLen in 1..id.length / 2) {
        val pattern = id.take(patternLen)
        val repeated = pattern.repeat(id.length / patternLen)

        if (id == repeated) return true
    }
    return false
}
