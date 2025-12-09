package day8

import utils.InputReader

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

    edges.sortWith(compareBy<Edge> { it.d }.thenBy { it.i }.thenBy { it.j })

    val uf = UnionFind(n)
    var components = n
    var lastConnection: Edge? = null

    for (e in edges) {
        if (uf.union(e.i, e.j)) {
            lastConnection = e
            components--

            if (components == 1) break
        }
    }

    val final = lastConnection ?: error("No final connection found!")

    val pA = points[final.i]
    val pB = points[final.j]

    val answer = 1L * pA.x * pB.x

    println("Count = $answer")
}