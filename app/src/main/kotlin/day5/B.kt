package day5

import day2.Range
import utils.InputReader
import kotlin.collections.mapNotNull

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(5, args)
    var newLineIndex = 0
    val ranges: List<IdRange> = lines.flatMap { line ->
        line.split(" ").mapNotNull { token ->
            val parts = token.split("-")
            if (parts.size == 2) {
                val start = parts[0].toLongOrNull()
                val end = parts[1].toLongOrNull()
                newLineIndex++
                if (start != null && end != null) IdRange(start, end) else null
            } else null
        }
    }
    val idRanges = lines.subList(0, newLineIndex)
    println("id ranges: $idRanges")
    val ingredients: List<Long> = lines.subList(newLineIndex + 1, lines.size).mapNotNull { it.toLongOrNull() }
    println("Ingredients: $ingredients")

    // Solve Problem
    val sorted = ranges.sortedBy { it.start }

    var merged = mutableListOf<IdRange>()
    var current = sorted.first()

    for (r in sorted.drop(1)) {
        if (r.start <= current.end + 1) {
            current = IdRange(current.start, maxOf(current.end, r.end))
        } else {
            merged += current
            current = r
        }
    }
    merged += current

    val totalFresh = merged.sumOf { it.end - it.start + 1 }
    println("Fresh Ids: $totalFresh")
}
