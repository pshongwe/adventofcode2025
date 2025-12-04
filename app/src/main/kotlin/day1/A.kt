package day1

import utils.InputReader

data class Rotation(val direction: Char, val distance: Int)

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(1, args)
    println("Read ${lines.size} lines")

    val rotations = lines.mapNotNull { line ->
        val trimmed = line.trim()
        if (trimmed.isNotEmpty()) {
            val direction = trimmed.first()
            val distance = trimmed.drop(1).toIntOrNull()
            if (distance != null) Rotation(direction, distance) else null
        } else null
    }

    println("Parsed ${rotations.size} rotations")
    println("First 5 rotations:")
    rotations.take(5).forEachIndexed { i, r -> println("${i + 1}: ${r.direction}${r.distance}") }

    // Solve Problem:
    var current = 50;
    var countZeros = 0;
    rotations.forEach() { rotation ->
        val a = current;
        val b = rotation.distance;

        if (rotation.direction == 'L') {
            current = (a - b) % 100;
        }
        else {
            current = (a + b) % 100;
        }

        if (current == 0) {
            countZeros++;
        }
    }
    println("Password: $countZeros")
}
