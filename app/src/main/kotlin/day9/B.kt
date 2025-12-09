package day9

import utils.InputReader
import java.util.*

data class Point(val x: Long, val y: Long)

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(9, args)

    val red = lines
        .filter { it.isNotEmpty() }
        .map { line ->
            val (x, y) = line.split(',').map { it.toLong() }
            Point(x, y)
        }

    // Solve Problem:
    val n = red.size
    if (n == 0) {
        println(0)
        return
    }

    val xs = mutableListOf<Long>()
    val ys = mutableListOf<Long>()

    fun add(v: Long, arr: MutableList<Long>) {
        arr.add(v)
        arr.add(v - 1)
        arr.add(v + 1)
    }

    for (p in red) {
        add(p.x, xs)
        add(p.y, ys)
    }

    for (i in 0 until n) {
        val a = red[i]
        val b = red[(i + 1) % n]

        if (a.y == b.y) {
            val y = a.y
            add(y, ys)
            val x1 = minOf(a.x, b.x)
            val x2 = maxOf(a.x, b.x)
            add(x1, xs)
            add(x2, xs)
        }

        if (a.x == b.x) {
            val x = a.x
            add(x, xs)
            val y1 = minOf(a.y, b.y)
            val y2 = maxOf(a.y, b.y)
            add(y1, ys)
            add(y2, ys)
        }
    }

    xs.sort()
    val uniqueXs = xs.distinct().sorted()

    ys.sort()
    val uniqueYs = ys.distinct().sorted()

    val W = uniqueXs.size
    val H = uniqueYs.size

    val grid = Array(H) { ByteArray(W) { 0 } }

    fun cx(X: Long): Int = uniqueXs.binarySearch(X)
    fun cy(Y: Long): Int = uniqueYs.binarySearch(Y)

    for (p in red) {
        val r = cy(p.y)
        val c = cx(p.x)
        grid[r][c] = 1
    }

    fun markSeg(a: Point, b: Point) {
        if (a.x == b.x) {
            // vertical
            val c = cx(a.x)
            val y1 = minOf(a.y, b.y)
            val y2 = maxOf(a.y, b.y)

            val r1 = cy(y1)
            val r2 = cy(y2)
            for (r in r1..r2) {
                if (grid[r][c] == 0.toByte()) grid[r][c] = 2
            }
        } else if (a.y == b.y) {
            val r = cy(a.y)
            val x1 = minOf(a.x, b.x)
            val x2 = maxOf(a.x, b.x)

            val c1 = cx(x1)
            val c2 = cx(x2)
            for (c in c1..c2) {
                if (grid[r][c] == 0.toByte()) grid[r][c] = 2
            }
        }
    }

    for (i in 0 until n) {
        markSeg(red[i], red[(i + 1) % n])
    }

    val vis = Array(H) { BooleanArray(W) { false } }
    val q = ArrayDeque<Pair<Int, Int>>()

    fun push(r: Int, c: Int) {
        if (r < 0 || r >= H || c < 0 || c >= W) return
        if (vis[r][c]) return
        if (grid[r][c] != 0.toByte()) return  // only flood EMPTY
        vis[r][c] = true
        q.add(Pair(r, c))
    }

    for (c in 0 until W) {
        push(0, c)
        push(H - 1, c)
    }
    for (r in 0 until H) {
        push(r, 0)
        push(r, W - 1)
    }

    val dr = intArrayOf(1, -1, 0, 0)
    val dc = intArrayOf(0, 0, 1, -1)

    while (q.isNotEmpty()) {
        val (r, c) = q.removeFirst()
        for (i in 0..3) {
            val nr = r + dr[i]
            val nc = c + dc[i]
            if (nr < 0 || nr >= H || nc < 0 || nc >= W) continue
            if (!vis[nr][nc] && grid[nr][nc] == 0.toByte()) {
                vis[nr][nc] = true
                q.add(Pair(nr, nc))
            }
        }
    }

    for (r in 0 until H) {
        for (c in 0 until W) {
            if (grid[r][c] == 0.toByte() && !vis[r][c]) grid[r][c] = 2
        }
    }

    var best = 0L

    for (i in 0 until n) {
        for (j in i + 1 until n) {
            val x1 = red[i].x
            val y1 = red[i].y
            val x2 = red[j].x
            val y2 = red[j].y

            if (x1 == x2 || y1 == y2) continue

            val cr1 = cy(y1)
            val cc1 = cx(x1)
            val cr2 = cy(y2)
            val cc2 = cx(x2)

            val rlo = minOf(cr1, cr2)
            val rhi = maxOf(cr1, cr2)
            val clo = minOf(cc1, cc2)
            val chi = maxOf(cc1, cc2)

            // Check validity
            var ok = true
            for (r in rlo..rhi) {
                if (!ok) break
                for (c in clo..chi) {
                    if (grid[r][c] == 0.toByte()) {
                        ok = false
                        break
                    }
                }
            }
            if (!ok) continue

            val realXmin = uniqueXs[clo]
            val realXmax = uniqueXs[chi]
            val realYmin = uniqueYs[rlo]
            val realYmax = uniqueYs[rhi]

            val width = realXmax - realXmin + 1
            val height = realYmax - realYmin + 1
            val area = width * height

            if (area > best) best = area
        }
    }
    println(best)
}