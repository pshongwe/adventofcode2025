package day5

import utils.InputReader
import kotlin.collections.mapNotNull

data class IdRange(val start: Long, val end: Long)

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
    var count = 0
    ingredients.forEach { i ->
        if (ranges.any { i in it.start..it.end }) {
            count++
        }
    }
    println("Fresh Ids: $count")

}
