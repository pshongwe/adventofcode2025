package utils

import java.nio.file.Files
import java.nio.file.Paths

object InputReader {
    /**
     * Read lines for a given day.
     * Resolution order:
     * 1. If `args` contains at least one entry, treat `args[0]` as a path and read it.
     * 2. Try to load resource `/inputs/day{day}{puzzle}.txt` from classpath resources.
     * 3. Fall back to stdin (interactive or piped).
     */
    fun readLinesForDay(day: Int, args: Array<String>): List<String> {
        if (args.isNotEmpty()) {
            val p = Paths.get(args[0])
            return Files.readAllLines(p)
        }

        val resourcePath = "/inputs/day$day.txt"
        val stream = InputReader::class.java.getResourceAsStream(resourcePath)
        if (stream != null) {
            return stream.bufferedReader().readLines()
        }

        println("No file arg and no resource '$resourcePath' found — reading stdin (end with EOF / Ctrl-D)")
        return generateSequence { kotlin.io.readLine() }.toList()
    }

    fun parseInts(lines: List<String>): List<Int> = lines.flatMap { line ->
        Regex("-?\\d+").findAll(line)
            .map { it.value.toInt() }
            .toList()
    }
}
