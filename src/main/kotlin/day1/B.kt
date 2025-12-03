package day1

import utils.InputReader


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
    var current = 50
    var countZeros = 0
    
    rotations.forEach { rotation ->
        if (rotation.direction == 'R') {
            for (step in 1..rotation.distance) {
                current = (current + 1) % 100
                if (current == 0) {
                    countZeros++
                }
            }
        } else if (rotation.direction == 'L') {
            for (step in 1..rotation.distance) {
                current = (current - 1 + 100) % 100
                if (current == 0) {
                    countZeros++
                }
            }
        }
    }
    println("Password: $countZeros")
}
