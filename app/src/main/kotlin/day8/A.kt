package day8

import utils.InputReader
import kotlin.math.sqrt

data class Point3D(val x: Int, val y: Int, val z: Int)
data class Edge(val i: Int, val j: Int, val d: Double)

class UnionFind(n: Int) {
    private val parent = IntArray(n) { it }
    private val size = IntArray(n) { 1 }

    fun find(a: Int): Int {
        if (parent[a] != a) parent[a] = find(parent[a])
        return parent[a]
    }

    fun union(a: Int, b: Int): Boolean {
        val ra = find(a)
        val rb = find(b)
        if (ra == rb) return false

        if (size[ra] < size[rb]) {
            parent[ra] = rb
            size[rb] += size[ra]
        } else {
            parent[rb] = ra
            size[ra] += size[rb]
        }

        return true
    }


    fun componentSizes(): List<Int> {
        val map = mutableMapOf<Int, Int>()
        for (i in parent.indices) {
            val root = find(i)
            map[root] = (map[root] ?: 0) + 1
        }
        return map.values.toList()
    }
}

fun distance(a: Point3D, b: Point3D): Double {
    val dx = (a.x - b.x).toDouble()
    val dy = (a.y - b.y).toDouble()
    val dz = (a.z - b.z).toDouble()
    return sqrt(dx*dx + dy*dy + dz*dz)
}

fun main(args: Array<String>) {
    // Prep Data:
    val lines = InputReader.readLinesForDay(8, args)
    val points = lines.map { line ->
        val (x, y, z) = line.split(",").map { it.trim().toInt() }
        Point3D(x, y, z)
    }

    val n = points.size

    // Solve Problem:

    val edges = ArrayList<Edge>()
    for (i in 0 until n) {
        for (j in i + 1 until n) {
            edges += Edge(i, j, distance(points[i], points[j]))
        }
    }

    edges.sortBy { it.d }

    val uf = UnionFind(n)
    edges.take(1000).forEach { uf.union(it.i, it.j) }

    val sizes = uf.componentSizes().sortedDescending()

    val answer = sizes.take(3).fold(1L) { acc, v -> acc * v }

    println("$answer")
}