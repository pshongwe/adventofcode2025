package day12

import utils.InputReader

private data class Shape(val idx: Int, val cells: List<Pair<Int, Int>>) {
    val area: Int get() = cells.size
}

private data class Region(val w: Int, val h: Int, val counts: IntArray)

private data class Mask(val words: LongArray)

private fun overlaps(a: LongArray, b: LongArray): Boolean {
    for (i in a.indices) if ((a[i] and b[i]) != 0L) return true
    return false
}

private fun orInto(dst: LongArray, add: LongArray) {
    for (i in dst.indices) dst[i] = dst[i] or add[i]
}

private fun parseAll(lines: List<String>): Pair<List<Shape>, List<Region>> {
    val shapes = mutableListOf<Shape>()
    val regions = mutableListOf<Region>()

    var i = 0

    fun isRegionLine(s: String): Boolean = Regex("""^\d+x\d+:""").containsMatchIn(s.trim())

    while (i < lines.size) {
        val line = lines[i].trim()
        if (line.isEmpty()) { i++; continue }
        if (isRegionLine(line)) break

        val m = Regex("""^(\d+):$""").matchEntire(line)
            ?: error("Expected shape header like '0:' but got: '${lines[i]}'")
        val idx = m.groupValues[1].toInt()
        i++

        val grid = mutableListOf<String>()
        while (i < lines.size) {
            val s = lines[i]
            if (s.trim().isEmpty()) { i++; break }
            if (Regex("""^\d+:$""").matches(s.trim()) || isRegionLine(s.trim())) break
            grid.add(s.trimEnd())
            i++
        }

        if (grid.isEmpty()) error("Shape $idx has empty grid")

        val cells = mutableListOf<Pair<Int, Int>>()
        for (r in grid.indices) {
            val row = grid[r]
            for (c in row.indices) {
                if (row[c] == '#') cells.add(r to c)
            }
        }
        shapes.add(Shape(idx, cells))
    }

    while (i < lines.size) {
        val line = lines[i].trim()
        i++
        if (line.isEmpty()) continue
        val parts = line.split(":")
        if (parts.size != 2) error("Bad region line: '$line'")
        val dims = parts[0].trim()
        val (wStr, hStr) = dims.split("x").let {
            if (it.size != 2) error("Bad dims: '$dims'")
            it[0] to it[1]
        }
        val w = wStr.toInt()
        val h = hStr.toInt()
        val counts = parts[1].trim().split(Regex("""\s+""")).filter { it.isNotEmpty() }.map { it.toInt() }.toIntArray()
        regions.add(Region(w, h, counts))
    }

    shapes.sortBy { it.idx }
    return shapes to regions
}

private fun uniqueOrientations(cells: List<Pair<Int, Int>>): List<List<Pair<Int, Int>>> {
    val transforms: List<(Int, Int) -> Pair<Int, Int>> = listOf(
        { x, y -> x to y },
        { x, y -> x to -y },
        { x, y -> -x to y },
        { x, y -> -x to -y },
        { x, y -> y to x },
        { x, y -> y to -x },
        { x, y -> -y to x },
        { x, y -> -y to -x }
    )

    val seen = HashSet<String>()
    val result = mutableListOf<List<Pair<Int, Int>>>()

    for (t in transforms) {
        val pts = cells.map { (x, y) -> t(x, y) }
        val minX = pts.minOf { it.first }
        val minY = pts.minOf { it.second }
        val norm = pts.map { (x, y) -> (x - minX) to (y - minY) }.sortedWith(compareBy({ it.first }, { it.second }))
        val key = norm.joinToString(";") { "${it.first},${it.second}" }
        if (seen.add(key)) result.add(norm)
    }
    return result
}

private fun buildPlacementsForShape(orientations: List<List<Pair<Int, Int>>>, w: Int, h: Int): List<Mask> {
    val n = w * h
    val wordsLen = (n + 63) / 64
    val placements = ArrayList<Mask>(1024)

    for (ori in orientations) {
        val maxR = ori.maxOf { it.first }
        val maxC = ori.maxOf { it.second }

        val maxTopR = h - (maxR + 1)
        val maxTopC = w - (maxC + 1)
        if (maxTopR < 0 || maxTopC < 0) continue

        for (tr in 0..maxTopR) {
            for (tc in 0..maxTopC) {
                val words = LongArray(wordsLen)
                for ((dr, dc) in ori) {
                    val r = tr + dr
                    val c = tc + dc
                    val bit = r * w + c
                    words[bit ushr 6] = words[bit ushr 6] or (1L shl (bit and 63))
                }
                placements.add(Mask(words))
            }
        }
    }

    return placements
}

private fun canPackRegion(
    w: Int,
    h: Int,
    shapes: List<Shape>,
    counts: IntArray
): Boolean {
    val required = ArrayList<Int>()
    for (s in shapes.indices) {
        val need = if (s < counts.size) counts[s] else 0
        repeat(need) { required.add(s) }
    }
    if (required.isEmpty()) return true

    val totalArea = required.sumOf { shapes[it].area }
    if (totalArea > w * h) return false

    val placementsByShape = Array(shapes.size) { emptyList<Mask>() }
    for (s in shapes.indices) {
        val oris = uniqueOrientations(shapes[s].cells)
        placementsByShape[s] = buildPlacementsForShape(oris, w, h)
        if (required.any { it == s } && placementsByShape[s].isEmpty()) return false
    }

    val piecePlacements: Array<List<Mask>> = Array(required.size) { idx ->
        placementsByShape[required[idx]]
    }

    val order = IntArray(required.size) { it }

    val nCells = w * h
    val occWordsLen = (nCells + 63) / 64

    fun dfs(pos: Int, occ: LongArray): Boolean {
        if (pos == order.size) return true

        var best = -1
        var bestCount = Int.MAX_VALUE
        for (k in pos until order.size) {
            val p = order[k]
            var count = 0
            for (m in piecePlacements[p]) {
                if (!overlaps(occ, m.words)) {
                    count++
                    if (count >= bestCount) break
                }
            }
            if (count == 0) return false
            if (count < bestCount) {
                bestCount = count
                best = k
                if (bestCount == 1) break
            }
        }

        val tmp = order[pos]
        order[pos] = order[best]
        order[best] = tmp

        val piece = order[pos]
        val masks = piecePlacements[piece]

        for (m in masks) {
            if (overlaps(occ, m.words)) continue
            val nextOcc = occ.copyOf()
            orInto(nextOcc, m.words)
            if (dfs(pos + 1, nextOcc)) {
                order[best] = order[pos]
                order[pos] = tmp
                return true
            }
        }

        order[best] = order[pos]
        order[pos] = tmp
        return false
    }

    return dfs(0, LongArray(occWordsLen))
}

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(12, args)

    // Solve Problem:
    val (shapes, regions) = parseAll(lines)

    var ok = 0
    for (r in regions) {
        if (canPackRegion(r.w, r.h, shapes, r.counts)) ok++
    }
    println(ok)
}